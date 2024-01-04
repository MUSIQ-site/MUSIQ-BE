package com.a608.musiq.domain.music.service;

import com.a608.musiq.domain.member.domain.MemberInfo;
import com.a608.musiq.domain.member.repository.MemberInfoRepository;
import com.a608.musiq.domain.music.data.Difficulty;
import com.a608.musiq.domain.music.domain.*;
import com.a608.musiq.domain.music.domain.log.SingleModeLog;
import com.a608.musiq.domain.music.dto.requestDto.AddIpInLogRequestDto;
import com.a608.musiq.domain.music.dto.responseDto.*;
import com.a608.musiq.domain.music.dto.responseDto.v2.*;
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
	private static final int LISTEN_NUM_LIMIT = 0;
	private static final int EASY_MODE_EXP_WEIGHT = 1;
	private static final int NORMAL_MODE_EXP_WEIGHT = 2;
	private static final int HARD_MODE_EXP_WEIGHT = 3;
	private static final int CRAZY_MODE_EXP_WEIGHT = 4;
	private static final int NUMBER_FOR_INITIALIZE = 0;
	private static final int DIFF_NUMBER_ROUND_TO_INDEX = 1;

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

		String year = EMPTY_STRING;
		String difficulty = EMPTY_STRING;
		int round = NUMBER_FOR_INITIALIZE;
		int life = NUMBER_FOR_INITIALIZE;

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
	@Transactional
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
			return DeletePrevGameResponseDto.of(Boolean.FALSE);
		}

		return DeletePrevGameResponseDto.of(Boolean.TRUE);
	}

	/**
	 * 게임 이어서 시작
	 * 
	 * @param token
	 * @return GameStartResponseDto
	 */
	@Override
	public RoundInfoResponseDto resumePrevGame(String token) {
		UUID memberId = jwtValidator.getData(token);

		// 현재 memberId가 이전에 종료되지 않은 게임이 있는지 Map에서 탐색
		boolean isExist = singleModeRoomManager.getRooms().containsKey(memberId);

		// 진행 중이었던 방이 있는 경우
		if(isExist) {
			SingleGameRoom room = singleModeRoomManager.getRooms().get(memberId);
			String url = room.getMusicList().get(room.getRound()-DIFF_NUMBER_ROUND_TO_INDEX).getUrl();
			return RoundInfoResponseDto.from(
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
	public RoundInfoResponseDto startNewGame(CreateRoomRequestServiceDto createRoomRequestServiceDto) {
		UUID memberId = jwtValidator.getData(createRoomRequestServiceDto.getToken());

		// 현재 진행 중인 게임이 있는지 Map에서 확인
		boolean isExist = singleModeRoomManager.getRooms().containsKey(memberId);
		if(isExist) {
			SingleGameRoom pastRoom = singleModeRoomManager.getRooms().get(memberId);
			gameEnd(pastRoom);
			singleModeRoomManager.getRooms().remove(memberId);
		}

		String year = createRoomRequestServiceDto.getYear();
		StringTokenizer stringTokenizer = new StringTokenizer(year, SPACE);
		Difficulty difficultyType = Difficulty.valueOf(createRoomRequestServiceDto.getDifficulty().toUpperCase());

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
		String url = room.getMusicList().get(room.getRound()-DIFF_NUMBER_ROUND_TO_INDEX).getUrl();

		return RoundInfoResponseDto.from(
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
			if(room.getListenNum() == LISTEN_NUM_LIMIT) {
				return MusicPlayCheckResponseDto.from(Boolean.FALSE, LISTEN_NUM_LIMIT);
			}
			// 듣기 횟수가 남아 있다면
			else {
				room.minusListenNum();
				return MusicPlayCheckResponseDto.from(Boolean.TRUE, room.getListenNum());
			}
		} else {
			throw new SingleModeException(SingleModeExceptionInfo.NOT_FOUND_LOG);
		}
	}

	/**
	 * 정답 채점
	 *
	 * @param token
	 * @param answer
	 * @return CheckAnswerResponseDto
	 */
	@Override
	public CheckAnswerResponseDto checkAnswer(String token, String answer) {
		UUID memberId = jwtValidator.getData(token);

		// 현재 진행 중인 게임이 있는지 Map에서 확인
		boolean isExist = singleModeRoomManager.getRooms().containsKey(memberId);

		// 진행 중인 게임이 있다면
		if(isExist) {
			SingleGameRoom room = singleModeRoomManager.getRooms().get(memberId);

			// 정답을 맞췄거나 시도횟수를 모두 사용하여 라운드가 끝난 상태이면 더 시도할 수 X
			if(room.getIsRoundEnded())
				throw new SingleModeException(SingleModeExceptionInfo.ENDED_ROUND);

			// 현재 라운드의 음악을 뽑아냄
			Music music = room.getMusicList().get(room.getRound()-DIFF_NUMBER_ROUND_TO_INDEX);

			// 해당 음악의 정답들을 가져옴
			List<Title> titles = titleRepository.findAllByMusicId(music.getId());

			// 채점을 위해 소문자로 변환, 공백을 제거
			answer = answer.toLowerCase().replaceAll(SPACE, EMPTY_STRING);

			// 정답 제목을 하나씩 돌면서 비교
			for(Title title : titles) {
				String musicTitle = title.getAnswer().toLowerCase().replaceAll(SPACE, EMPTY_STRING);

				// 일치하는 정답이 있는 경우
				if(answer.equals(musicTitle)) {
					room.roundEnd();
					return CheckAnswerResponseDto.from(Boolean.TRUE, room.getIsRoundEnded(), room.getTryNum());
				}
			}

			// 일치하는 정답이 없는 경우
			room.minusTryNum();
			return CheckAnswerResponseDto.from(Boolean.FALSE, room.getIsRoundEnded(), room.getTryNum());

		} else {
			throw new SingleModeException(SingleModeExceptionInfo.NOT_FOUND_LOG);
		}
	}

	/**
	 * 라운드 스킵
	 *
	 * @param token
	 * @return SingleSkipResponseDto
	 */
	@Override
	public SingleSkipResponseDto skipRound(String token) {
		UUID memberId = jwtValidator.getData(token);

		// 현재 진행 중인 게임이 있는지 Map에서 확인
		boolean isExist = singleModeRoomManager.getRooms().containsKey(memberId);

		// 존재한다면
		if(isExist) {
			SingleGameRoom room = singleModeRoomManager.getRooms().get(memberId);

			// 이미 라운드가 종료된 상태라면 중복으로 처리 X
			// 종료된 방 안내 예외 발생
			if(room.getIsRoundEnded()) {
				throw new SingleModeException(SingleModeExceptionInfo.ENDED_ROUND);
			}

			// 라운드의 상태를 종료 상태로 바꾸고
			room.roundEnd();
			// 목숨 - 1
			room.minusLife();
			return SingleSkipResponseDto.of(Boolean.TRUE);
		}
		else {
			throw new SingleModeException(SingleModeExceptionInfo.NOT_FOUND_LOG);
		}
	}

	/**
	 * 현재 라운드 종료
	 *
	 * @param token
	 * @return SingleRoundEndResponseDto
	 */
	@Override
	public SingleRoundEndResponseDto endRound(String token) {
		UUID memberId = jwtValidator.getData(token);

		// 현재 진행 중인 게임이 있는지 Map에서 확인
		boolean isExist = singleModeRoomManager.getRooms().containsKey(memberId);

		if(isExist) {
			SingleGameRoom room = singleModeRoomManager.getRooms().get(memberId);
			boolean isRoundEnd = room.getIsRoundEnded();

			// 만약 라운드가 종료되지 않은 상태에서 호출되었다면 로직 수행 X
			if(!isRoundEnd) {
				throw new SingleModeException(SingleModeExceptionInfo.ONGOING_ROUND);
			}

			// 전체 게임 종료 여부
			boolean isGameOver = false;
			if(room.getLife() == 0 || room.getRound() == room.getMusicList().size())
				isGameOver = true;

			// 현재 문제
			Music music = room.getMusicList().get(room.getRound()-DIFF_NUMBER_ROUND_TO_INDEX);

			return SingleRoundEndResponseDto.from(
					music.getTitle(),
					music.getSinger(),
					room.getRound(),
					room.getLife(),
					isGameOver
			);
		}
		else {
			throw new SingleModeException(SingleModeExceptionInfo.NOT_FOUND_LOG);
		}
	}

	/**
	 * 다음 라운드
	 *
	 * @param token
	 * @return RoundInfoResponseDto
	 */
	@Override
	public RoundInfoResponseDto nextRound(String token) {
		UUID memberId = jwtValidator.getData(token);

		// 현재 진행 중인 게임이 있는지 Map에서 확인
		boolean isExist = singleModeRoomManager.getRooms().containsKey(memberId);

		if(isExist) {
			SingleGameRoom room = singleModeRoomManager.getRooms().get(memberId);
			boolean isRoundEnd = room.getIsRoundEnded();

			// 만약 라운드가 종료되지 않은 상태에서 호출되었다면 로직 수행 X
			if(!isRoundEnd) {
				throw new SingleModeException(SingleModeExceptionInfo.ONGOING_ROUND);
			}

			// 만약 목숨이 0이거나 마지막 곡이라면 더이상 진행할 수 없으므로 예외 throw
			if(room.getLife() == 0 || room.getRound() == room.getMusicList().size())
				throw new SingleModeException(SingleModeExceptionInfo.GAME_OVER);

			// 다음 라운드를 위한 변수 초기화
			// 라운드+1, 듣기횟수 3, 시도횟수 3, 문제종료여부 false
			room.goNextRound();

			// 다음 문제
			Music music = room.getMusicList().get(room.getRound()-DIFF_NUMBER_ROUND_TO_INDEX);

			return RoundInfoResponseDto.from(
					room.getDifficulty().getValue(),
					room.getRound(),
					room.getLife(),
					room.getTryNum(),
					room.getListenNum(),
					music.getUrl()
			);
		}
		else {
			throw new SingleModeException(SingleModeExceptionInfo.NOT_FOUND_LOG);
		}
	}

	/**
	 * 게임 종료
	 *
	 * @param token
	 * @return SingleGameOverResponseDto
	 */
	@Override
	@Transactional
	public SingleGameOverResponseDto gameOver(String token) {
		UUID memberId = jwtValidator.getData(token);

		// 현재 진행 중인 게임이 있는지 Map에서 확인
		boolean isExist = singleModeRoomManager.getRooms().containsKey(memberId);

		if(isExist) {
			SingleGameRoom room = singleModeRoomManager.getRooms().get(memberId);
			boolean isRoundEnd = room.getIsRoundEnded();

			// 만약 라운드가 종료되지 않은 상태에서 호출되었다면 로직 수행 X
			if(!isRoundEnd) {
				throw new SingleModeException(SingleModeExceptionInfo.ONGOING_ROUND);
			}

			// 만약 목숨이 남거나 마지막 곡이 아니라면 예외 throw
			if(room.getLife() > 0 && room.getRound() < room.getMusicList().size())
				throw new SingleModeException(SingleModeExceptionInfo.NOT_GAME_OVER);

			// 게임 종료 처리
			gameEnd(room);

			// Map에서 삭제
			singleModeRoomManager.getRooms().remove(memberId);

			return SingleGameOverResponseDto.from(
					room.getYear(),
					room.getDifficulty().getValue(),
					room.getRound()
			);
		}
		else {
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
