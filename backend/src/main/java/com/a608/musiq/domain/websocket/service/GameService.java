package com.a608.musiq.domain.websocket.service;

import com.a608.musiq.domain.member.domain.MemberInfo;
import com.a608.musiq.domain.member.repository.MemberInfoRepository;
import com.a608.musiq.domain.websocket.data.GameRoomType;
import com.a608.musiq.domain.websocket.data.GameValue;
import com.a608.musiq.domain.websocket.data.MessageDtoType;
import com.a608.musiq.domain.websocket.data.MessageType;
import com.a608.musiq.domain.websocket.data.PlayType;
import com.a608.musiq.domain.websocket.domain.Channel;
import com.a608.musiq.domain.websocket.domain.GameRoom;
import com.a608.musiq.domain.websocket.domain.UserInfoItem;
import com.a608.musiq.domain.websocket.domain.log.MultiModeCreateGameRoomLog;
import com.a608.musiq.domain.websocket.domain.log.MultiModeGameOverLog;
import com.a608.musiq.domain.websocket.domain.log.MultiModeGameStartLog;
import com.a608.musiq.domain.websocket.dto.GetUserInfoItemDto;
import com.a608.musiq.domain.websocket.dto.requestDto.CheckPasswordRequestDto;
import com.a608.musiq.domain.websocket.dto.requestDto.ExitGameRoomRequestDto;
import com.a608.musiq.domain.websocket.dto.requestDto.GameOverRequestDto;
import com.a608.musiq.domain.websocket.dto.requestDto.GameStartRequestDto;
import com.a608.musiq.domain.websocket.dto.responseDto.AllChannelSizeResponseDto;
import com.a608.musiq.domain.websocket.dto.responseDto.ChannelUserResponseDto;
import com.a608.musiq.domain.websocket.dto.responseDto.ChannelUserResponseItem;
import com.a608.musiq.domain.websocket.domain.ChatMessage;
import com.a608.musiq.domain.websocket.dto.responseDto.EnterGameRoomResponseDto;
import com.a608.musiq.domain.websocket.dto.responseDto.GameOverResponseDto;
import com.a608.musiq.domain.websocket.dto.responseDto.GameRoomListResponseDto;
import com.a608.musiq.domain.websocket.dto.responseDto.GameRoomListResponseItem;
import com.a608.musiq.domain.websocket.dto.gameMessageDto.BeforeAnswerCorrectDto;
import com.a608.musiq.domain.websocket.dto.gameMessageDto.EnterGameRoomDto;
import com.a608.musiq.domain.websocket.dto.gameMessageDto.GameRoomPubDto;
import com.a608.musiq.domain.websocket.dto.gameMessageDto.GameRoomMemberInfo;
import com.a608.musiq.domain.websocket.dto.gameMessageDto.ChatMessagePubDto;
import com.a608.musiq.domain.websocket.dto.gameMessageDto.GameResultDto;
import com.a608.musiq.domain.websocket.dto.gameMessageDto.GameResultItem;
import com.a608.musiq.domain.websocket.dto.gameMessageDto.GameStartPubDto;
import com.a608.musiq.domain.websocket.dto.gameMessageDto.TimeDto;
import com.a608.musiq.domain.websocket.dto.gameMessageDto.ExitGameRoomDto;
import com.a608.musiq.domain.websocket.dto.responseDto.CheckPasswordResponseDto;
import com.a608.musiq.domain.websocket.dto.responseDto.GameStartResponseDto;
import com.a608.musiq.domain.websocket.repository.MultiModeCreateGameRoomLogRepository;
import com.a608.musiq.domain.websocket.repository.MultiModeGameOverLogRepository;
import com.a608.musiq.domain.websocket.repository.MultiModeGameStartLogRepository;
import com.a608.musiq.domain.websocket.service.subService.AfterAnswerService;
import com.a608.musiq.domain.websocket.service.subService.BeforeAnswerService;
import com.a608.musiq.domain.websocket.service.subService.CommonService;
import com.a608.musiq.domain.websocket.service.subService.RoundStartService;
import com.a608.musiq.global.Util;
import com.a608.musiq.global.Util.RedisKey;
import com.a608.musiq.domain.websocket.dto.requestDto.CreateGameRoomRequestDto;
import com.a608.musiq.domain.websocket.dto.responseDto.CreateGameRoomResponseDto;
import com.a608.musiq.domain.websocket.dto.responseDto.DisconnectSocketResponseDto;
import com.a608.musiq.domain.websocket.dto.responseDto.ExitGameRoomResponse;
import com.a608.musiq.global.exception.exception.MemberInfoException;
import com.a608.musiq.global.exception.exception.MultiModeException;
import com.a608.musiq.global.exception.info.MemberInfoExceptionInfo;
import com.a608.musiq.global.exception.info.MultiModeExceptionInfo;
import com.a608.musiq.global.jwt.JwtValidator;

import jakarta.annotation.PostConstruct;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameService {

	private static final Logger logger = LoggerFactory.getLogger(GameService.class);

	private final JwtValidator jwtValidator;
	private final Util util;
	private final MemberInfoRepository memberInfoRepository;
	private final MultiModeCreateGameRoomLogRepository multiModeCreateGameRoomLogRepository;
	private final MultiModeGameStartLogRepository multiModeGameStartLogRepository;
	private final MultiModeGameOverLogRepository multiModeGameOverLogRepository;

	private final RoundStartService roundStartService;
	private final BeforeAnswerService beforeAnswerService;
	private final AfterAnswerService afterAnswerService;
	private final CommonService commonService;

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	private ReentrantReadWriteLock lock;

	private static final int LEVEL_SIZE = 50;
	private static final int MAKING_LOBBY_CHANNEL_NO = 1000;

	@PostConstruct
	public void init() {
		this.lock = new ReentrantReadWriteLock();
	}

	/**
	 * @param accessToken
	 * @param channelNo
	 */
	@Async("asyncThreadPool")
	public void joinGameChannel(String accessToken, int channelNo) {
		UUID uuid = jwtValidator.getData(accessToken);

		logger.info("MaxSize = {}", GameValue.getGameChannelEachMaxSize());
		logger.info("CurrentChannelSize = {}", GameValue.getGameChannelSize(channelNo));

		// 정원 초과 확인
		if (GameValue.getGameChannelSize(channelNo) > GameValue.getGameChannelEachMaxSize()) {
			throw new MultiModeException(MultiModeExceptionInfo.INVALID_JOIN_REQUEST);
		}

		try {
			lock.writeLock().lock();
			GameValue.addUserToChannel(uuid, channelNo);
		} finally {
			lock.writeLock().unlock();
		}

	}

	/**
	 * @param accessToken
	 * @return
	 * @see AllChannelSizeResponseDto
	 */
	public AllChannelSizeResponseDto getAllChannelSizeList(String accessToken) {
		AllChannelSizeResponseDto allChannelSizeResponseDto = new AllChannelSizeResponseDto();
		List<Integer> list = new ArrayList<>();

		for (int i = 1; i <= GameValue.getGameChannelMaxSize(); i++) {
			list.add(GameValue.getGameChannelSize(i));
		}

		allChannelSizeResponseDto.setChannelSizes(list);
		return allChannelSizeResponseDto;
	}

	/**
	 * @param accessToken
	 * @param channelNo
	 */
	public DisconnectSocketResponseDto disconnectUser(String accessToken, int channelNo) {
		UUID uuid = jwtValidator.getData(accessToken);

        int curChannelNo = GameValue.getGameChannel(channelNo).get(uuid);

        if(channelNo == curChannelNo) {
            GameValue.removeUserFromChannel(uuid, channelNo);
        }

		return DisconnectSocketResponseDto.builder().channelNo(channelNo).build();
	}

	/**
	 * @param channelNo
	 * @param chatMessage
	 */
	public void sendMessage(int channelNo, ChatMessage chatMessage, String accessToken) {

		UUID uuid = jwtValidator.getData(accessToken);

		String destination = getDestination(channelNo);

		logger.info("nickname : {} , message : {}", chatMessage.getNickname(),
			chatMessage.getMessage());

		if (channelNo <= 10) {
			ChatMessagePubDto chatMessagePubDto = ChatMessagePubDto.create(MessageDtoType.CHAT,
				chatMessage.getNickname(), chatMessage.getMessage());
			messagingTemplate.convertAndSend(destination, chatMessagePubDto);
			return;
		}

		//방 번호로 gameRoom 객체 조회
		GameRoom gameRoom = GameValue.getGameRooms().get(channelNo);

		// 게임이 시작될 때의 로직
		if (chatMessage.getMessageType().equals(MessageType.GAMESTART)) {

			// 게임방 시작 초기화
			gameRoom.initializeGameStart();

			// 문제 출제
			gameRoom.setMultiModeProblems(
				roundStartService.makeMutiProblemList(gameRoom.getNumberOfProblems(),
					gameRoom.getYear()));

			List<GameRoomMemberInfo> memberInfos = gameRoom.getUserInfoItems().values().stream()
				.map(item -> GameRoomMemberInfo.builder()
					.nickName(item.getNickname())
					.build()).toList();

			GameStartPubDto dto = GameStartPubDto.builder()
				.memberInfos(memberInfos)
				.build();

			// 게임 시작 pub
			messagingTemplate.convertAndSend(destination, dto);
			return;
		}

		//게임룸 타입 가져오기 - 게임 시작은 http 통신으로 민구가 WAITING에서 GAME으로 바꿔줄거임
		GameRoomType gameRoomType = gameRoom.getGameRoomType();

		if (gameRoomType == GameRoomType.WAITING || gameRoomType == GameRoomType.END) {
			//일반 채팅
			ChatMessagePubDto chatMessagePubDto = ChatMessagePubDto.create(MessageDtoType.CHAT,
				chatMessage.getNickname(), chatMessage.getMessage());
			messagingTemplate.convertAndSend(destination, chatMessagePubDto);
			return;
		}
		if (gameRoomType == GameRoomType.GAME) {
			PlayType playType = gameRoom.getPlayType();
			if (playType == PlayType.ROUNDSTART) {
				//일반 채팅
				ChatMessagePubDto chatMessagePubDto = ChatMessagePubDto.create(MessageDtoType.CHAT,
					chatMessage.getNickname(), chatMessage.getMessage());
				messagingTemplate.convertAndSend(destination, chatMessagePubDto);
				return;

			}
			if (playType == PlayType.BEFOREANSWER) {

				// 스킵 확인
				if (chatMessage.getMessage().equals(".")) {
					// 이미 스킵 했으면 그냥 return
					if (gameRoom.getUserInfoItems().get(uuid).getIsSkipped()) {
						return;
					}

					//먼저 일반채팅으로 pub 부터 함
					ChatMessagePubDto chatMessagePubDto = ChatMessagePubDto.create(
						MessageDtoType.CHAT, chatMessage.getNickname(),
						chatMessage.getMessage());
					messagingTemplate.convertAndSend(destination, chatMessagePubDto);

					//beforeAnswer 일때 스킵 로직 구현
					beforeAnswerService.skip(gameRoom, uuid, destination);

					return;

				}
				// 스킵이 아닌 경우
				else {

					//먼저 일반채팅으로 pub 부터 함
					ChatMessagePubDto chatMessagePubDto = ChatMessagePubDto.create(
						MessageDtoType.CHAT, chatMessage.getNickname(),
						chatMessage.getMessage());
					messagingTemplate.convertAndSend(destination, chatMessagePubDto);

					//그 다음 정답 채점 로직 구현
					int round = gameRoom.getRound() - 1;
					String submitedAnswer = chatMessage.getMessage().replaceAll(" ", "")
						.toLowerCase();
					for (String answer : gameRoom.getMultiModeProblems().get(round)
						.getAnswerList()) {
						//정답 맞은 경우
						answer = answer.replaceAll(" ", "").toLowerCase();
						if (submitedAnswer.equals(answer.toLowerCase())) {

							gameRoom.setPlayType(PlayType.AFTERANSWER);

							String title = gameRoom.getMultiModeProblems().get(round).getTitle();
							String singer = gameRoom.getMultiModeProblems().get(round).getSinger();

							List<GameRoomMemberInfo> memberInfos = new ArrayList<>();
							//스킵 투표 초기화
							gameRoom.setSkipVote(0);
							//gameRoom의 UserInfoItems의 isSkiped 모두 false로 업데이트
							// 모든 유저 현재 스코어 dto에 담아서 pub
							for (UUID userUuid : gameRoom.getUserInfoItems().keySet()) {
								UserInfoItem userInfoItem = gameRoom.getUserInfoItems()
									.get(userUuid);
								userInfoItem.setSkipped(false);
								//정답자는 score 올리기
								if (userInfoItem.getNickname().equals(chatMessage.getNickname())) {
									userInfoItem.upScore();
								}
								GameRoomMemberInfo memberInfo = GameRoomMemberInfo.create(userInfoItem.getNickname(),
									userInfoItem.getScore());
								memberInfos.add(memberInfo);
							}
							// 정답자 닉네임, 정답 제목, 가수, skipVote 0 pub, 유저의 모든 닉네임, 스코어 pub
							BeforeAnswerCorrectDto beforeAnswerCorrectDto = BeforeAnswerCorrectDto.create(
								MessageDtoType.BEFOREANSWERCORRECT, chatMessage.getNickname(),
								title, singer, 0, memberInfos);
							messagingTemplate.convertAndSend(destination, beforeAnswerCorrectDto);

							return;
						}
					}
				}
			}

			if (playType == PlayType.AFTERANSWER) {

				if (chatMessage.getMessage().equals(".")) {
					// 이미 스킵 했으면 그냥 return
					if (gameRoom.getUserInfoItems().get(uuid).getIsSkipped()) {
						return;
					}
					afterAnswerService.skip(gameRoom, uuid, destination);
				}

				//일반 채팅
				ChatMessagePubDto chatMessagePubDto = ChatMessagePubDto.create(
					MessageDtoType.CHAT, chatMessage.getNickname(),
					chatMessage.getMessage());
				messagingTemplate.convertAndSend(destination, chatMessagePubDto);
			}
		}

		logger.info("Message send success / Destination : {}", destination);

		//        if(chatMessage.getMessageType() == MessageType.GAME) {
		//            submitAnswer(chatMessage.getMessage());
		//        }
	}

	public void pubMessage() {
		Map<Integer, GameRoom> rooms = GameValue.getGameRooms();
		Set<Integer> roomNums = rooms.keySet();

		for (Integer roomNum : roomNums) {

			if (roomNum <= 10) {
				continue;
			}

			GameRoom room = rooms.get(roomNum);

			// GameRoom Type 대기 상태인 경우는 처리하지 않음
			if (room.getGameRoomType() == GameRoomType.WAITING) {
				continue;
			}

			// 게임 중인 경우
			else if (room.getGameRoomType() == GameRoomType.GAME) {
				switch (room.getPlayType()) {

					case ROUNDSTART:
						roundStartService.doRoundStart(roomNum, room);
						break;
					case BEFOREANSWER:
						beforeAnswerService.doBeforeAnswer(roomNum, room);
						break;
					case AFTERANSWER:
						afterAnswerService.doAfterAnswer(roomNum, room);
						break;
				}
			}

			// 게임이 종료되었다면
			else {
				Map<UUID, UserInfoItem> userInfoMap = room.getUserInfoItems();

				if (room.getTime() == 10) {
					// 참여 인원의 점수들을 리스트로 통합
					List<GameResultItem> gameResults = new ArrayList<>(userInfoMap.values().stream()
						.map(item -> GameResultItem.builder().nickname(item.getNickname())
							.score(item.getScore()).build()).collect(Collectors.toList()));

                    // 리스트 정렬
                    gameResults.sort((o1, o2)->{return o2.getScore().compareTo(o1.getScore());});

                    // 점수 리스트를 담아 전송
                    GameResultDto dto = GameResultDto.builder().userResults(gameResults).build();

					messagingTemplate.convertAndSend("/topic/" + roomNum, dto);

					// 경험치 정산
					for (UUID memberId : userInfoMap.keySet()) {

						Optional<MemberInfo> memberInfoOptional = memberInfoRepository.findById(
							memberId);
						if (memberInfoOptional.isEmpty()) {
							continue;
						}

						// Transactional을 위한 UpdateExp 메서드 분리
						MemberInfo memberInfo = memberInfoOptional.get();
						commonService.updateExp(memberInfo, userInfoMap.get(memberId).getScore());

						// 저장
						memberInfoRepository.save(memberInfo);

						util.insertDatatoRedisSortedSet(RedisKey.RANKING.getKey(),
							memberInfo.getNickname(), memberInfo.getExp());
					}
				}

				// 시간 초 카운트
				if (room.getTime() > 0) {

					// 타임을 객체에 담아서
					TimeDto dto = TimeDto.builder().time(room.getTime()).build();
					messagingTemplate.convertAndSend("/topic/" + roomNum, dto);

					room.timeDown();
				}
				// 남은 시간이 0이라면
				else {

					List<GameRoomMemberInfo> memberInfos = new ArrayList<>();

					for (UUID memberId : userInfoMap.keySet()) {
						Optional<MemberInfo> memberInfoOptional = memberInfoRepository.findById(
							memberId);
						if (memberInfoOptional.isEmpty()) {
							continue;
						}
						MemberInfo memberInfo = memberInfoOptional.get();

						memberInfos.add(
							GameRoomMemberInfo.builder()
								.nickName(memberInfo.getNickname())
								.build());
					}

					// 다음 판을 위한 세팅
					room.initializeGameEnd();

					// 클라이언트에게 대기방 관련 정보 전달 해줘야 함
					GameRoomPubDto dto = GameRoomPubDto.builder()
						.memberInfos(memberInfos)
						.roomNo(room.getRoomNo())
						.roomName(room.getRoomName())
						.password(room.getPassword())
						.isPrivate(room.isPrivate())
						.numberOfProblems(room.getNumberOfProblems())
						.year(room.getYear())
						.roomManagerNickname(room.getRoomManagerNickname())
						.build();

					messagingTemplate.convertAndSend("/topic/" + roomNum, dto);
				}
			}
		}
	}

	/**
	 * @param channelNo
	 * @return
	 */
	private String getDestination(int channelNo) {
		return "/topic/" + channelNo;
	}

	/**
	 * @param accessToken
	 * @param channelNo
	 * @return
	 * @see ChannelUserResponseDto
	 */
	public ChannelUserResponseDto getUserList(String accessToken, int channelNo) {
		ChannelUserResponseDto channelUserResponseDto = new ChannelUserResponseDto();
		List<ChannelUserResponseItem> items = new ArrayList<>();
		ConcurrentHashMap<UUID, Integer> channel = GameValue.getGameChannel(channelNo);
		Iterator<UUID> it = channel.keySet().iterator();

        while (it.hasNext()) {
            UUID uuid = it.next();
            MemberInfo memberInfo = memberInfoRepository.findById(uuid).orElseThrow(
                    () -> new MemberInfoException(MemberInfoExceptionInfo.NOT_FOUND_MEMBER_INFO));
            logger.info("Nickname : {}, channel : {}", memberInfo.getNickname(), channel.get(uuid));
            items.add(ChannelUserResponseItem.builder().nickname(memberInfo.getNickname())
                    .userLevel((int) (memberInfo.getExp() / 50) + 1)
                    .isGaming(channel.get(uuid) / 1000 != 0).build());
        }

		channelUserResponseDto.setChannelUserResponseItems(items);

		return channelUserResponseDto;
	}

	/**
	 * @param accessToken
	 * @param channelNo
	 * @return
	 * @see GameRoomListResponseDto
	 */
	public GameRoomListResponseDto getGameRoomList(String accessToken, int channelNo) {
		ConcurrentHashMap<Integer, GameRoom> gameRooms = GameValue.getGameRooms();

		Iterator<Integer> it = gameRooms.keySet().iterator();
		List<GameRoomListResponseItem> gameRoomListResponseItems = new ArrayList<>();

		while (it.hasNext()) {
			int subscribeNo = it.next();
			logger.info("subscribeNo = {}", subscribeNo);
			if ((subscribeNo / 1000) == channelNo) {
				GameRoom gameRoom = gameRooms.get(subscribeNo);
				MemberInfo roomManager = memberInfoRepository.findById(
					gameRoom.getRoomManagerUUID()).orElseThrow(() -> new MemberInfoException(
					MemberInfoExceptionInfo.NOT_FOUND_MEMBER_INFO));
				List<String> years = Arrays.stream(gameRoom.getYear().split(" ")).toList();
				gameRoomListResponseItems.add(
					GameRoomListResponseItem.builder()
						.gameRoomNo(subscribeNo)
						.roomTitle(gameRoom.getRoomName())
						.roomManager(roomManager.getNickname())
						.currentMembers(gameRoom.getTotalUsers())
						.quizAmount(gameRoom.getNumberOfProblems())
						.isPrivate(!gameRoom.getPassword().equals(""))
						.isPlay(gameRoom.getGameRoomType().equals(GameRoomType.GAME))
						.years(years)
						.build());
			}
		}

		return GameRoomListResponseDto.builder().rooms(gameRoomListResponseItems).build();
	}

	public CreateGameRoomResponseDto makeGameRoom(String accessToken,
		CreateGameRoomRequestDto createGameRoomRequestDto) {
		UUID uuid = jwtValidator.getData(accessToken);
		MemberInfo memberInfo = memberInfoRepository.findById(uuid).orElseThrow(
			() -> new MemberInfoException(MemberInfoExceptionInfo.NOT_FOUND_MEMBER_INFO));

		Channel channel = GameValue.getChannel(createGameRoomRequestDto.getChannelNo());
		int curRoomIndex = channel.getMinimumEmptyRoomNo();
		int roomNumber = createGameRoomRequestDto.getChannelNo() * 1000 + curRoomIndex;

		Map<UUID, UserInfoItem> userInfoItems = new HashMap<>();
		userInfoItems.put(uuid,
			UserInfoItem.builder().nickname(memberInfo.getNickname()).score(0.0)
				.isSkipped(false).build());
		GameRoom gameRoom = GameRoom.builder().roomNo(roomNumber)
			.roomName(createGameRoomRequestDto.getRoomName())
			.password(createGameRoomRequestDto.getPassword())
			.isPrivate(!createGameRoomRequestDto.getPassword().equals(""))
			.roomManagerUUID(uuid)
			.roomManagerNickname(memberInfo.getNickname())
			.numberOfProblems(createGameRoomRequestDto.getQuizAmount())
			.year(createGameRoomRequestDto.getMusicYear())
			.totalUsers(0)
			.gameRoomType(GameRoomType.WAITING)
			.userInfoItems(userInfoItems).build();

		// 게임방 생성 로그 저장
		int multiModeCreateGameRoomLogId = saveMultiModeCreateGameRoomLog(createGameRoomRequestDto,
			memberInfo.getNickname());

		channel.removeUser(uuid);
		channel.addUser(uuid, roomNumber);
		GameValue.addGameChannel(roomNumber,
			gameRoom);
		logger.info("Create GameRoom Successful");
		channel.updateIsUsed(curRoomIndex);

		GameRoomMemberInfo gameRoomMemberInfo = GameRoomMemberInfo.builder()
			.nickName(memberInfo.getNickname())
			.build();
		List<GameRoomMemberInfo> gameRoomMemberInfos = new ArrayList<>();
		gameRoomMemberInfos.add(gameRoomMemberInfo);

		// 메세지 펍 해주기
		//일반 채팅

		String destination =
			"/topic/" + roomNumber;

		GameRoomPubDto gameRoomPubDto = GameRoomPubDto.builder()
			.memberInfos(gameRoomMemberInfos)
			.roomNo(roomNumber)
			.roomName(createGameRoomRequestDto.getRoomName())
			.password(createGameRoomRequestDto.getPassword())
			.isPrivate(!gameRoom.getPassword().equals(""))
			.numberOfProblems(createGameRoomRequestDto.getQuizAmount())
			.year(createGameRoomRequestDto.getMusicYear())
			.roomManagerNickname(memberInfo.getNickname())
			.build();

		messagingTemplate.convertAndSend(destination, gameRoomPubDto);

		return CreateGameRoomResponseDto.builder()
			.gameRoomNo(roomNumber)
			.multiModeCreateGameRoomLogId(multiModeCreateGameRoomLogId)
			.build();
	}

	/**
	 * 비밀번호 체크
	 *
	 * @param checkPasswordRequestDto
	 * @return
	 */
	public CheckPasswordResponseDto checkPassword(CheckPasswordRequestDto checkPasswordRequestDto) {
		GameRoom gameRoom = GameValue.getGameRooms().get(checkPasswordRequestDto.getGameRoomNo());

		return commonService.checkPassword(gameRoom, checkPasswordRequestDto.getPassword());
	}

	/**
	 * 게임방 입장
	 *
	 * @param accessToken
	 * @param gameRoomNo
	 * @return
	 */
	public EnterGameRoomResponseDto enterGameRoom(String accessToken, int gameRoomNo) {
		UUID uuid = jwtValidator.getData(accessToken);
		String nickname = memberInfoRepository.findNicknameById(uuid)
			.orElseThrow(() -> new MemberInfoException(
				MemberInfoExceptionInfo.NOT_FOUND_MEMBER_INFO));

		GameRoom gameRoom = GameValue.getGameRooms().get(gameRoomNo);

		return commonService.enterGameRoom(uuid, nickname, gameRoom, gameRoomNo);
	}

	/**
	 * 게임방 입장 발행
	 *
	 * @param channelNo
	 */
	public void enterGameRoomForPublish(String accessToken, int channelNo) {
		UUID uuid = jwtValidator.getData(accessToken);
		String destination = getDestination(channelNo);
		GameRoom gameRoom = GameValue.getGameRooms().get(channelNo);

		EnterGameRoomDto enterGameRoomDto = commonService.enterGameRoomForPublish(uuid, gameRoom);

		messagingTemplate.convertAndSend(destination, enterGameRoomDto);
	}

	/**
	 * 게임방 나가기
	 *
	 * @param accessToken
	 * @param exitGameRoomRequestDto
	 * @return ExitGameRoomResponse
	 */
	public ExitGameRoomResponse exitGameRoom(String accessToken, ExitGameRoomRequestDto exitGameRoomRequestDto) {
		// previousChannelNo : from -> 게임 방 번호
		int previousChannelNo = exitGameRoomRequestDto.getPreviousChannelNo();
		// destinationChannelNo : to -> 로비 번호
		int destinationChannelNo = previousChannelNo / MAKING_LOBBY_CHANNEL_NO;
		UUID uuid = jwtValidator.getData(accessToken);

		String nickname = memberInfoRepository.findNicknameById(uuid)
			.orElseThrow(() -> new MemberInfoException(MemberInfoExceptionInfo.NOT_FOUND_MEMBER_INFO));

		//pubDestination == previousChannelNo : pub 해줄 destination
		String pubDestination = getDestination(previousChannelNo);
		GameRoom gameRoom = GameValue.getGameRooms().get(previousChannelNo);

		ExitGameRoomDto exitGameRoomDto = commonService.exitGameRoom(uuid, nickname, gameRoom, previousChannelNo);

		messagingTemplate.convertAndSend(pubDestination, exitGameRoomDto);

		ExitGameRoomResponse exitGameRoomResponse = ExitGameRoomResponse.builder()
			.destinationChannelNo(destinationChannelNo)
			.build();

		return exitGameRoomResponse;
	}

	/**
	 * 게임방 생성 로그 저장
	 *
	 * @param createGameRoomRequestDto
	 * @param nickname
	 * @see CreateGameRoomRequestDto
	 * @return int
	 */
	private int saveMultiModeCreateGameRoomLog(CreateGameRoomRequestDto createGameRoomRequestDto, String nickname) {
		return multiModeCreateGameRoomLogRepository.save(MultiModeCreateGameRoomLog.builder()
				.title(createGameRoomRequestDto.getRoomName())
				.years(createGameRoomRequestDto.getMusicYear())
				.roomManagerNickname(nickname)
				.isStarted(Boolean.FALSE)
				.createdAt(LocalDateTime.now())
				.build())
			.getId();
	}

	/**
	 * 게임 시작 로그 저장
	 *
	 * @param gameStartRequestDto
	 * @see GameStartRequestDto
	 * @return GameStartResponseDto
	 */
	@Transactional
	public GameStartResponseDto saveMultiModeGameStartLog(GameStartRequestDto gameStartRequestDto) {
		GameRoom gameRoom = GameValue.getGameRooms().get(gameStartRequestDto.getGameRoomNumber());

		// 게임방 생성 isStart 업데이트
		updateIsStartOfMultiModeCreateGameRoomLog(gameStartRequestDto);

		// 게임 시작 로그 생성
		int multiModeCreateGameRoomLogId = multiModeGameStartLogRepository.save(MultiModeGameStartLog.builder()
			.multiModeCreateGameRoomLogId(gameStartRequestDto.getMultiModeCreateGameRoomLogId())
			.title(gameRoom.getRoomName())
			.years(gameRoom.getYear())
			.roomManagerNickname(gameRoom.getRoomManagerNickname())
			.nicknames(gameRoom.getNicknames())
			.startedAt(LocalDateTime.now())
			.build()).getMultiModeCreateGameRoomLogId();

		return GameStartResponseDto.builder()
			.multiModeCreateGameRoomLogId(multiModeCreateGameRoomLogId)
			.build();
	}

	/**
	 * 게임방 생성 isStart 업데이트
	 *
	 * @param gameStartRequestDto
	 * @see GameStartRequestDto
	 */
	private void updateIsStartOfMultiModeCreateGameRoomLog(GameStartRequestDto gameStartRequestDto) {
		// 게임방 생성 로그 게임 시작으로 update
		MultiModeCreateGameRoomLog multiModeCreateGameRoomLog = multiModeCreateGameRoomLogRepository.findById(
			gameStartRequestDto.getMultiModeCreateGameRoomLogId()).orElseThrow(() ->
			new MultiModeException(MultiModeExceptionInfo.NOT_FOUND_MULTI_MODE_CREATE_GAME_ROOM_LOG));

		multiModeCreateGameRoomLog.gameStart();
		multiModeCreateGameRoomLogRepository.save(multiModeCreateGameRoomLog);
	}

	/**
	 * 게임 종료 로그
	 *
	 * @param gameOverRequestDto
	 * @see GameOverRequestDto
	 * @return GameOverResponseDto
	 */
	@Transactional
	public GameOverResponseDto saveMultiModeGameOverLog(GameOverRequestDto gameOverRequestDto) {
		LocalDateTime latestStartedAt = getLatestStartedAt(gameOverRequestDto);
		LocalDateTime endedAt = LocalDateTime.now();
		int playTime = Long.valueOf(Duration.between(latestStartedAt, endedAt).getSeconds()).intValue();

		GameRoom gameRoom = GameValue.getGameRooms().get(gameOverRequestDto.getGameRoomNumber());

		GetUserInfoItemDto userInfoItemDto = gameRoom.getUserInfoItemDto();

		int multiModeCreateGameRoomLogId = multiModeGameOverLogRepository.save(MultiModeGameOverLog.builder()
			.multiModeCreateGameRoomLogId(gameOverRequestDto.getMultiModeCreateGameRoomLogId())
			.title(gameRoom.getRoomName())
			.years(gameRoom.getYear())
			.nicknames(userInfoItemDto.getNicknames())
			.exps(userInfoItemDto.getExps())
			.endedAt(endedAt)
			.playTime(playTime)
			.build()).getMultiModeCreateGameRoomLogId();
		
		 // 게임방 사용자 점수 초기화
		gameRoom.gameRoomUserScoreReset();

		return GameOverResponseDto.builder()
			.multiModeCreateGameRoomLogId(multiModeCreateGameRoomLogId)
			.build();
	}

	/**
	 * 게임 시작 로그에서 startedAt 가져오기
	 *
	 * @param gameOverRequestDto
	 * @see GameOverRequestDto
	 * @return LocalDateTime
	 */
	private LocalDateTime getLatestStartedAt(GameOverRequestDto gameOverRequestDto) {
		return multiModeGameStartLogRepository.findLatestStartedAtByMultiModeCreateGameRoomLogId(
				gameOverRequestDto.getMultiModeCreateGameRoomLogId())
			.orElseThrow(() -> new MultiModeException(MultiModeExceptionInfo.NOT_FOUND_MULTI_MODE_GAME_START_LOG));
	}

}
