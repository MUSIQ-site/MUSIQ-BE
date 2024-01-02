package com.a608.musiq.domain.music.service;

import com.a608.musiq.domain.member.domain.MemberInfo;
import com.a608.musiq.domain.member.repository.MemberInfoRepository;
import com.a608.musiq.domain.music.data.Difficulty;
import com.a608.musiq.domain.music.domain.*;
import com.a608.musiq.domain.music.domain.log.SingleModeLog;
import com.a608.musiq.domain.music.dto.requestDto.AddIpInLogRequestDto;
import com.a608.musiq.domain.music.dto.responseDto.*;
import com.a608.musiq.domain.music.dto.responseDto.v2.CheckPrevGameResponseDto;
import com.a608.musiq.domain.music.dto.responseDto.v2.DeletePrevGameResponseDto;
import com.a608.musiq.domain.music.dto.responseDto.v2.GameStartResponseDto;
import com.a608.musiq.domain.music.dto.responseDto.v2.MusicPlayCheckResponseDto;
import com.a608.musiq.domain.music.dto.serviceDto.CreateRoomRequestServiceDto;
import com.a608.musiq.domain.music.repository.MusicRepository;
import com.a608.musiq.domain.music.repository.SingleModeLogRepository;
import com.a608.musiq.domain.music.repository.TitleRepository;
import com.a608.musiq.global.Util;
import com.a608.musiq.global.Util.RedisKey;
import com.a608.musiq.global.exception.exception.MemberInfoException;
import com.a608.musiq.global.exception.exception.MusicException;
import com.a608.musiq.global.exception.exception.SingleModeException;
import com.a608.musiq.global.exception.info.MemberInfoExceptionInfo;
import com.a608.musiq.global.exception.info.MusicExceptionInfo;
import com.a608.musiq.global.exception.info.SingleModeExceptionInfo;
import com.a608.musiq.global.jwt.JwtValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SingleModeMusicServiceImplV2 implements SingleModeMusicService {
	private static final String SPACE = " ";
	private static final String EMPTY_STRING = "";
	private static final int EMPTY_LIST_SIZE = 0;
	private static final int LOOP_START_INDEX = 0;
	private static final int EASY_MODE_EXP_WEIGHT = 1;
	private static final int NORMAL_MODE_EXP_WEIGHT = 2;
	private static final int HARD_MODE_EXP_WEIGHT = 3;
	private static final int CRAZY_MODE_EXP_WEIGHT = 4;

	private final SingleModeRoomManagerV2 singleModeRoomManager = new SingleModeRoomManagerV2();

	private final MemberInfoRepository memberInfoRepository;
	private final MusicRepository musicRepository;
	private final TitleRepository titleRepository;
	private final SingleModeLogRepository singleModeLogRepository;
	private final JwtValidator jwtValidator;
	private final Util util;

	/**
	 * 방에 노래 추가
	 *
	 * @param stringTokenizer
	 * @return List<Music>
	 */
	private List<Music> insertMusic(StringTokenizer stringTokenizer) {
		List<Music> musicList = new ArrayList<>();

		while (stringTokenizer.hasMoreTokens()) {
			List<Music> eachMusicListByYear = musicRepository.findAllByYear(stringTokenizer.nextToken());
			musicList.addAll(eachMusicListByYear);
		}
		int musicListSize = musicList.size();

		if (musicListSize == EMPTY_LIST_SIZE) {
			throw new MusicException(MusicExceptionInfo.INVALID_YEAR);
		}

		return deleteDuplicatedMusic(musicList);
	}

	/**
	 * 중복된 노래 제거
	 *
	 * @param musicList
	 * @return List<Music>
	 */
	private List<Music> deleteDuplicatedMusic(List<Music> musicList) {
		Set<String> titleSet = new HashSet<>();
		Set<String> singerSet = new HashSet<>();
		List<Music> finalMusicList = new ArrayList<>();

		for (int i = LOOP_START_INDEX; i < musicList.size(); i++) {
			Music nowMusic = musicList.get(i);

			int beforeTitleSetSize = titleSet.size();
			titleSet.add(nowMusic.getTitle());
			int afterTitleSetSize = titleSet.size();

			int beforeSingerSetSize = singerSet.size();
			singerSet.add(nowMusic.getSinger());
			int afterSingerSetSize = singerSet.size();

			if (beforeTitleSetSize == afterTitleSetSize
					&& beforeSingerSetSize == afterSingerSetSize) {
				continue;
			}

			finalMusicList.add(nowMusic);
		}

		return finalMusicList;
	}

	/**
	 * 로그에 ip 추가
	 *
	 * @param addIpInLogRequestDto
	 * @see AddIpInLogResponseDto
	 * @return AddIpInLogResponseDto
	 */
	@Override
	@Transactional
	public AddIpInLogResponseDto addIpInLog(AddIpInLogRequestDto addIpInLogRequestDto) {
		SingleModeLog log = singleModeLogRepository.findById(addIpInLogRequestDto.getRoomId())
			.orElseThrow(() -> new SingleModeException(SingleModeExceptionInfo.NOT_FOUND_LOG));

		log.addIp(addIpInLogRequestDto.getUserIp());

		return AddIpInLogResponseDto.of(log.getIp());
	}

	/**
	 * 이전 게임 존재 유무 확인
	 *
	 * @param token
	 * @return CheckPrevGameResponseDto
	 */
	@Override
	public CheckPrevGameResponseDto checkPrevGame(String token) {
		UUID memberId = jwtValidator.getData(token);
		
		// 현재 memberId가 이전에 종료되지 않은 게임이 있는지 Map에서 탐색
		boolean isExist = singleModeRoomManager.getRooms().containsKey(memberId);

		String year = "";
		String difficulty = "";
		int round = 0;
		int life = 0;

		// 있는 경우
		if(isExist) {
			SingleGameRoom room = singleModeRoomManager.getRooms().get(memberId);
			year = room.getYear();
			difficulty = room.getDifficulty().getValue();
			round = room.getRound();
			life = room.getLife();
		}

		return CheckPrevGameResponseDto.from(isExist, year, difficulty, round, life);
	}

	/**
	 * 이전 게임 삭제
	 *
	 * @param token
	 * @returnDeletePrevGameResponseDto
	 */
	@Override
	public DeletePrevGameResponseDto deletePrevGame(String token) {
		UUID memberId = jwtValidator.getData(token);

		// 현재 memberId가 이전에 종료되지 않은 게임이 있는지 Map에서 탐색
		boolean isExist = singleModeRoomManager.getRooms().containsKey(memberId);

		try {
			// 진행 중이었던 방이 있는 경우
			if(isExist) {
				// 게임 종료 로직을 수행함 (경험치, 로그 관련)
				SingleGameRoom room = singleModeRoomManager.getRooms().get(memberId);
				gameEnd(room);

				// Map에서 데이터 삭제
				singleModeRoomManager.getRooms().remove(memberId);
			} else {
				throw new SingleModeException(SingleModeExceptionInfo.NOT_FOUND_LOG);
			}
		} catch (Exception e) {
			return DeletePrevGameResponseDto.of(false);
		}

		return DeletePrevGameResponseDto.of(true);
	}

	/**
	 * 게임 이어서 시작
	 * 
	 * @param token
	 * @return GameStartResponseDto
	 */
	@Override
	public GameStartResponseDto resumePrevGame(String token) {
		UUID memberId = jwtValidator.getData(token);

		// 현재 memberId가 이전에 종료되지 않은 게임이 있는지 Map에서 탐색
		boolean isExist = singleModeRoomManager.getRooms().containsKey(memberId);

		// 진행 중이었던 방이 있는 경우
		if(isExist) {
			SingleGameRoom room = singleModeRoomManager.getRooms().get(memberId);
			String url = room.getMusicList().get(room.getRound()-1).getUrl();
			return GameStartResponseDto.from(
					room.getDifficulty().getValue(),
					room.getRound(),
					room.getLife(),
					room.getTryNum(),
					room.getListenNum(),
					url
			);
		} else {
			throw new SingleModeException(SingleModeExceptionInfo.NOT_FOUND_LOG);
		}
	}

	/**
	 * 새로운 게임 시작 (방 생성)
	 *
	 * @param createRoomRequestServiceDto
	 * @see CreateRoomRequestServiceDto
	 * @see CreateRoomResponseDto
	 * @return CreateRoomResponseDto
	 */
	@Override
	@Transactional
	public GameStartResponseDto startNewGame(CreateRoomRequestServiceDto createRoomRequestServiceDto) {
		String year = createRoomRequestServiceDto.getYear();
		StringTokenizer stringTokenizer = new StringTokenizer(year, SPACE);
		Difficulty difficultyType = Difficulty.valueOf(createRoomRequestServiceDto.getDifficulty().toUpperCase());

		UUID memberId = jwtValidator.getData(createRoomRequestServiceDto.getToken());

		String nickname = memberInfoRepository.findNicknameById(memberId)
			.orElseThrow(() -> new MemberInfoException(MemberInfoExceptionInfo.NOT_FOUND_MEMBER_INFO));

		SingleModeLog log = SingleModeLog.from(
			createRoomRequestServiceDto.getYear(), difficultyType, memberId, nickname);

		int roomId = singleModeLogRepository.save(log).getId();

		List<Music> musicList = insertMusic(stringTokenizer);
		Collections.shuffle(musicList);
		SingleGameRoom room = SingleGameRoom.from(musicList, difficultyType, year, roomId);
		singleModeRoomManager.addRoom(memberId, room);

		// 첫번째 문제의 url
		String url = room.getMusicList().get(room.getRound()-1).getUrl();

		return GameStartResponseDto.from(
				room.getDifficulty().getValue(),
				room.getRound(),
				room.getLife(),
				room.getTryNum(),
				room.getListenNum(),
				url
		);
	}

	/**
	 * 노래 재생 가능 여부 확인
	 * 
	 * @param token
	 * @return MusicPlayCheckResponseDto
	 */
	@Override
	public MusicPlayCheckResponseDto checkMusicPlay(String token) {
		UUID memberId = jwtValidator.getData(token);

		// 현재 진행 중인 게임이 있는지 Map에서 확인
		boolean isExist = singleModeRoomManager.getRooms().containsKey(memberId);

		if(isExist) {
			SingleGameRoom room = singleModeRoomManager.getRooms().get(memberId);
			// 듣기 횟수가 남아 있지 않다면
			if(room.getListenNum() == 0) {
				return MusicPlayCheckResponseDto.from(false, 0);
			}
			// 듣기 횟수가 남아 있다면
			else {
				room.minusListenNum();
				return MusicPlayCheckResponseDto.from(true, room.getListenNum());
			}
		} else {
			throw new SingleModeException(SingleModeExceptionInfo.NOT_FOUND_LOG);
		}
	}

	/**
	 * 게임 끝 로직
	 *
	 * @param room
	 */
	@Transactional
	public void gameEnd(SingleGameRoom room) {
		SingleModeLog log = singleModeLogRepository.findById(room.getRoomId())
			.orElseThrow(() -> new SingleModeException(SingleModeExceptionInfo.NOT_FOUND_LOG));

		MemberInfo memberInfo = memberInfoRepository.findById(log.getMemberId())
			.orElseThrow(() -> new MemberInfoException(MemberInfoExceptionInfo.NOT_FOUND_MEMBER_INFO));

		// 경험치 작업
		double exp = calculateExp(log.getDifficulty(), room.getRound());
		memberInfo.gainExp(exp);

		double totalExp = memberInfo.getExp();

		util.insertDatatoRedisSortedSet(RedisKey.RANKING.getKey(), memberInfo.getNickname(), totalExp);

		// 로그 추가 데이터 입력
		log.addAdditionalInformation(room.getRound(), exp);
	}

	/**
	 * 경험치 계산
	 *
	 * @param difficulty
	 * @param round
	 * @return double
	 */
	private double calculateExp(Difficulty difficulty, int round) {
		if (difficulty.equals(Difficulty.EASY)) {
			return EASY_MODE_EXP_WEIGHT * round;
		}

		if (difficulty.equals(Difficulty.CRAZY)) {
			return CRAZY_MODE_EXP_WEIGHT * round;
		}

		if (difficulty.equals(Difficulty.HARD)) {
			return HARD_MODE_EXP_WEIGHT * round;
		}

		return NORMAL_MODE_EXP_WEIGHT * round;
	}
}
