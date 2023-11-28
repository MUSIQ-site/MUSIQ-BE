package com.a608.musiq.domain.music.service;

import com.a608.musiq.global.Util;
import com.a608.musiq.global.Util.RedisKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.a608.musiq.domain.member.domain.MemberInfo;
import com.a608.musiq.domain.member.repository.MemberInfoRepository;
import com.a608.musiq.domain.music.data.Difficulty;
import com.a608.musiq.domain.music.domain.Music;
import com.a608.musiq.domain.music.domain.Room;
import com.a608.musiq.domain.music.domain.SingleModeRoomManager;
import com.a608.musiq.domain.music.domain.Title;
import com.a608.musiq.domain.music.domain.log.SingleModeLog;
import com.a608.musiq.domain.music.dto.requestDto.AddIpInLogRequestDto;
import com.a608.musiq.domain.music.dto.responseDto.AddIpInLogResponseDto;
import com.a608.musiq.domain.music.dto.responseDto.CreateRoomResponseDto;
import com.a608.musiq.domain.music.dto.responseDto.GameOverResponseDto;
import com.a608.musiq.domain.music.dto.responseDto.GiveUpResponseDto;
import com.a608.musiq.domain.music.dto.responseDto.GradeAnswerResponseDto;
import com.a608.musiq.domain.music.dto.responseDto.GetProblemsResponseDto;
import com.a608.musiq.domain.music.dto.responseDto.SkipRoundResponseDto;
import com.a608.musiq.domain.music.dto.serviceDto.CreateRoomRequestServiceDto;
import com.a608.musiq.domain.music.repository.MusicRepository;
import com.a608.musiq.domain.music.repository.SingleModeLogRepository;
import com.a608.musiq.domain.music.repository.TitleRepository;
import com.a608.musiq.global.exception.exception.MemberInfoException;
import com.a608.musiq.global.exception.exception.MusicException;
import com.a608.musiq.global.exception.exception.SingleModeException;
import com.a608.musiq.global.exception.info.MemberInfoExceptionInfo;
import com.a608.musiq.global.exception.info.MusicExceptionInfo;
import com.a608.musiq.global.exception.info.SingleModeExceptionInfo;
import com.a608.musiq.global.jwt.JwtValidator;

import lombok.RequiredArgsConstructor;

@Service("singleModeMusicServiceImpl")
@RequiredArgsConstructor
public class SingleModeMusicServiceImpl implements MusicService {
	private static final String SPACE = " ";
	private static final String EMPTY_STRING = "";
	private static final int EMPTY_LIST_SIZE = 0;
	private static final int LOOP_START_INDEX = 0;
	private static final int EASY_MODE_EXP_WEIGHT = 1;
	private static final int NORMAL_MODE_EXP_WEIGHT = 2;
	private static final int HARD_MODE_EXP_WEIGHT = 3;
	private static final int CRAZY_MODE_EXP_WEIGHT = 4;

	private final SingleModeRoomManager singleModeRoomManager = new SingleModeRoomManager();

	private final MemberInfoRepository memberInfoRepository;
	private final MusicRepository musicRepository;
	private final TitleRepository titleRepository;
	private final SingleModeLogRepository singleModeLogRepository;
	private final JwtValidator jwtValidator;
	private final Util util;

	/**
	 * 싱글 모드 방 생성
	 *
	 * @param createRoomRequestServiceDto
	 * @see CreateRoomRequestServiceDto
	 * @see CreateRoomResponseDto
	 * @return CreateRoomResponseDto
	 */
	@Override
	@Transactional
	public CreateRoomResponseDto createRoom(CreateRoomRequestServiceDto createRoomRequestServiceDto) {
		StringTokenizer stringTokenizer = new StringTokenizer(createRoomRequestServiceDto.getYear(), SPACE);
		Difficulty difficultyType = Difficulty.valueOf(createRoomRequestServiceDto.getDifficulty().toUpperCase());

		UUID memberId = jwtValidator.getData(createRoomRequestServiceDto.getToken());

		String nickname = memberInfoRepository.findNicknameById(memberId)
			.orElseThrow(() -> new MemberInfoException(MemberInfoExceptionInfo.NOT_FOUND_MEMBER_INFO));

		SingleModeLog log = SingleModeLog.from(
			createRoomRequestServiceDto.getYear(), difficultyType, memberId, nickname);

		int roomId = singleModeLogRepository.save(log).getId();

		List<Music> musicList = insertMusic(stringTokenizer);
		Collections.shuffle(musicList);
		Room room = Room.from(musicList, difficultyType);
		singleModeRoomManager.addRoom(roomId, room);

		return CreateRoomResponseDto.from(roomId, musicList.size());
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
	 * 게스트 모드 문제 출제
	 *
	 * @param roomId
	 * @param round
	 * @see GetProblemsResponseDto
	 * @return GetProblemsResponseDto
	 */
	@Override
	public GetProblemsResponseDto getProblem(int roomId, int round) {
		Room room = singleModeRoomManager.getRooms().get(roomId);

		Music music = room.getMusicList().get(round);

		return GetProblemsResponseDto.create(room.getDifficulty(), music.getId(),
			music.getUrl(), round);
	}

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
	 * 정답 채점
	 *
	 * @param roomId
	 * @param round
	 * @param answer
	 * @return GradeAnswerResponseDto
	 * @see GradeAnswerResponseDto
	 */
	@Override
	public GradeAnswerResponseDto gradeAnswer(int roomId, int round, String answer) {
		Room room = singleModeRoomManager.getRooms().get(roomId);

		if (room.getRound() != round) {
			throw new MusicException(MusicExceptionInfo.INVALID_ROUND);
		}

		Music music = room.getMusicList().get(round);
		List<Title> titles = titleRepository.findAllByMusicId(music.getId());

		answer = answer.toLowerCase().replaceAll(SPACE, EMPTY_STRING);

		GradeAnswerResponseDto gradeAnswerResponseDto = null;
		for (Title title : titles) {
			String musicTitle = title.getAnswer().toLowerCase().replace(SPACE, EMPTY_STRING);

			if (answer.equals(musicTitle)) {
				room.addRound(round);
				gradeAnswerResponseDto = GradeAnswerResponseDto.from(Boolean.TRUE, room.getRound(), music);
				break;
			}
		}

		if (gradeAnswerResponseDto == null) {
			gradeAnswerResponseDto = GradeAnswerResponseDto.from(Boolean.FALSE, room.getRound(),
				music);
		}

		return gradeAnswerResponseDto;
	}

	/**
	 * 라운드 스킵
	 *
	 * @param roomId
	 * @param round
	 * @see SkipRoundResponseDto
	 * @return SkipRoundResponseDto
	 */
	@Override
	public SkipRoundResponseDto skipRound(int roomId, int round) {
		Room room = singleModeRoomManager.getRooms().get(roomId);
		String title = room.getMusicList().get(round).getTitle();
		String singer = room.getMusicList().get(round).getSinger();

		room.addRound(round);

		return SkipRoundResponseDto.from(room.getRound(), title, singer);
	}

	/**
	 * 게임 종료
	 *
	 * @param roomId
	 * @param round
	 * @see GameOverResponseDto
	 * @return GameOverResponseDto
	 */
	@Override
	@Transactional
	public GameOverResponseDto gameOver(int roomId, int round) {
		SingleModeLog log = singleModeLogRepository.findById(roomId)
			.orElseThrow(() -> new SingleModeException(SingleModeExceptionInfo.NOT_FOUND_LOG));

		MemberInfo memberInfo = memberInfoRepository.findById(log.getMemberId())
			.orElseThrow(() -> new MemberInfoException(MemberInfoExceptionInfo.NOT_FOUND_MEMBER_INFO));

		double exp = calculateExp(log.getDifficulty(), round);
		memberInfo.gainExp(exp);

		double totalExp = memberInfo.getExp();

		util.insertDatatoRedisSortedSet(RedisKey.RANKING.getKey(), memberInfo.getNickname(), totalExp);

		log.addAdditionalInformation(round, exp);

		return GameOverResponseDto.from(round, exp);
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

	/**
	 * 게임 포기
	 *
	 * @param roomId
	 * @param round
	 * @see GiveUpResponseDto
	 * @return GiveUpResponseDto
	 */
	@Override
	public GiveUpResponseDto giveUp(int roomId, int round) {
		Music music = singleModeRoomManager.getRooms().get(roomId).getMusicList().get(round);
		String title = music.getTitle();
		String singer = music.getSinger();

		return GiveUpResponseDto.from(title, singer);
	}

}
