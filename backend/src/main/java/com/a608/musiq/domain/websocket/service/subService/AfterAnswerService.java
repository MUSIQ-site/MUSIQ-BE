package com.a608.musiq.domain.websocket.service.subService;

import com.a608.musiq.domain.websocket.data.GameRoomType;
import com.a608.musiq.domain.websocket.data.MessageDtoType;
import com.a608.musiq.domain.websocket.data.PlayType;
import com.a608.musiq.domain.websocket.domain.GameRoom;
import com.a608.musiq.domain.websocket.domain.UserInfoItem;
import com.a608.musiq.domain.websocket.dto.gameMessageDto.MusicEndDto;
import com.a608.musiq.domain.websocket.dto.gameMessageDto.TimeDto;
import java.util.Map;
import java.util.UUID;
import com.a608.musiq.domain.websocket.dto.gameMessageDto.SkipVoteDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AfterAnswerService {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    private static final int MAKING_HALF_NUMBER = 2;
    private static final int MAKING_CEIL_NUMBER = 1;
    private static final int SKIP_VOTE_INITIAL_NUMBER = 0;

    public void doAfterAnswer(Integer roomNum, GameRoom room) {


        // 남은 시간이 1초 이상이라면 시간 다운
        if (room.getTime() > 0) {

            // 카운트 다운 전송
            TimeDto dto = TimeDto.builder()
                    .time(room.getTime())
                    .message(room.getTime() + " 초")
                    .build();
            messagingTemplate.convertAndSend("/topic/" + roomNum, dto);

            room.timeDown();
        }
        // 0초인 경우
        else {
            if (room.getRound() >= room.getNumberOfProblems()) {
                room.changeGameRoomType(GameRoomType.END);
                room.setTime(10);
            } else {
                room.changePlayType(PlayType.ROUNDSTART);
                room.roundUp();
                room.setTime(5);
            }

            // 참여 인원의 스킵 여부를 모두 false로 바꿈
            Map<UUID, UserInfoItem> userInfos = room.getUserInfoItems();
            for (UserInfoItem user : userInfos.values()) {
                user.setSkipped(false);
            }

            // 방의 전체 스킵 수도 0으로 설정
            room.setSkipVote(0);

            // 음악 종료 신호 전송
            MusicEndDto dto = MusicEndDto.builder().build();
            messagingTemplate.convertAndSend("/topic/" + roomNum, dto);
        }
    }

    public void skip(GameRoom gameRoom, UUID uuid, String destination) {
        int skipVote = gameRoom.getSkipVote();
        //게임 룸 skipVote ++
        gameRoom.setSkipVote(++skipVote);
        //해당 유저 isSkipped = true
        gameRoom.getUserInfoItems().get(uuid).setSkipped(true);

        //과반수인 경우
        if (gameRoom.getSkipVote() >= (gameRoom.getTotalUsers() / MAKING_HALF_NUMBER
                + MAKING_CEIL_NUMBER)) {

            gameRoom.setTime(SKIP_VOTE_INITIAL_NUMBER);
            SkipVoteDto skipVoteDto = SkipVoteDto.create(MessageDtoType.AFTERSKIP, true,
                    gameRoom.getSkipVote());

            // skipVote++ 하고 pub
            messagingTemplate.convertAndSend(destination, skipVoteDto);
        } else {
            //과반수가 아닌 경우
            SkipVoteDto skipVoteDto = SkipVoteDto.create(MessageDtoType.AFTERSKIP, false,
                    gameRoom.getSkipVote());
            // skipVote++ 하고 pub
            messagingTemplate.convertAndSend(destination, skipVoteDto);
        }
    }
}
