package com.a608.musiq.domain.websocket.domain;

import com.a608.musiq.domain.websocket.data.GameRoomType;
import com.a608.musiq.domain.websocket.data.GameValue;
import com.a608.musiq.domain.websocket.data.MessageDtoType;
import com.a608.musiq.domain.websocket.data.MessageType;
import com.a608.musiq.domain.websocket.data.PlayType;
import com.a608.musiq.domain.websocket.dto.GetUserInfoItemDto;
import com.a608.musiq.domain.websocket.dto.gameMessageDto.GameRoomMemberInfo;
import com.a608.musiq.domain.websocket.dto.responseDto.CheckPasswordResponseDto;
import com.a608.musiq.domain.websocket.dto.responseDto.EnterGameRoomResponseDto;
import com.a608.musiq.domain.websocket.dto.gameMessageDto.EnterGameRoomDto;
import com.a608.musiq.domain.websocket.dto.gameMessageDto.ExitGameRoomDto;
import com.a608.musiq.global.exception.exception.MultiModeException;
import com.a608.musiq.global.exception.info.MultiModeExceptionInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameRoom {
    private static final int LEAST_MEMBER_SIZE = 1;
    private static final int ROOM_DIVIDE_NUMBER = 1000;
    private static final int MAX_ROOM_USER = 6;
    private static final int MULTI_MODE_EXP_WEIGHT = 10;
    private static final String SPACE = " ";

    private int roomNo;
    private String roomName;

    private String password;
    private boolean isPrivate;
    

    // 방장
    private UUID roomManagerUUID;
    //문제 수
    private int numberOfProblems;

    //선택한 연도
    private String year;

    private String roomManagerNickname;


    private GameRoomType gameRoomType;
    //------------------------------------------------
    private Map<UUID, UserInfoItem> userInfoItems;
    //currentMembers
    private int totalUsers;
    //------------------------------------------------
    private PlayType playType;

    private int time;


    private int skipVote;
    private int round;



    private List<MultiModeProblem> multiModeProblems;
    private MessageType messageType;


    //answerList 추가 (정답리스트)
    //gameRoomId 추가

    public void setMultiModeProblems(
        List<MultiModeProblem> multiModeProblems) {
        this.multiModeProblems = multiModeProblems;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setPlayType(PlayType playType) {
        this.playType = playType;
    }

    public void setSkipVote(int skipVote) {
        this.skipVote = skipVote;
    }

    public void changeGameRoomType(GameRoomType type) {
        this.gameRoomType = type;
    }

    public void changePlayType(PlayType type) {
        this.playType = type;
    }

    public void timeDown() {
        this.time--;
    }

    public void roundUp() {
        this.round++;
    }

    public ExitGameRoomDto exitUser(UUID uuid, String nickname, int gameRoomNumber) {
        int lobbyChannelNumber = gameRoomNumber / ROOM_DIVIDE_NUMBER;
        int gameRoomIndex = gameRoomNumber % ROOM_DIVIDE_NUMBER;
        List<GameRoomMemberInfo> gameRoomMemberInfos = new ArrayList<>();
        
        // 방에 아무도 안 남을 경우
        if (totalUsers == LEAST_MEMBER_SIZE) {
            GameValue.deleteGameRoom(lobbyChannelNumber, gameRoomIndex, gameRoomNumber);

            this.totalUsers--;
            this.userInfoItems.remove(uuid);

            for(UserInfoItem userInfoItem : this.userInfoItems.values()) {
                GameRoomMemberInfo gameRoomMemberInfo =
                    GameRoomMemberInfo.create(userInfoItem.getNickname(),userInfoItem.getScore());
                gameRoomMemberInfos.add(gameRoomMemberInfo);
            }


            return ExitGameRoomDto.builder()
                .messageType(MessageDtoType.EXITUSER)
                .userInfoItems(gameRoomMemberInfos)
                .gameRoomManagerNickname(this.roomManagerNickname)
                .exitedUserNickname(nickname)
                .build();
        }


        // 방장 위임
        if (uuid.equals(this.roomManagerUUID)) {
            for(UUID userUUID : this.userInfoItems.keySet()) {
                if (!userUUID.equals(this.roomManagerUUID)) {
                    this.roomManagerNickname = userInfoItems.get(userUUID).getNickname();
                    this.roomManagerUUID = userUUID;
                    break;
                }
            }
        }

        this.totalUsers--;
        userInfoItems.remove(uuid);

        for(UserInfoItem userInfoItem : this.userInfoItems.values()) {
            GameRoomMemberInfo gameRoomMemberInfo =
                GameRoomMemberInfo.create(userInfoItem.getNickname(),userInfoItem.getScore());
            gameRoomMemberInfos.add(gameRoomMemberInfo);
        }


        return ExitGameRoomDto.builder()
            .messageType(MessageDtoType.EXITUSER)
            .userInfoItems(gameRoomMemberInfos)
            .gameRoomManagerNickname(this.roomManagerNickname)
            .exitedUserNickname(nickname)
            .build();
    }

    public CheckPasswordResponseDto checkPassword(String password) {
        if (!this.isPrivate) {
            return new CheckPasswordResponseDto(Boolean.TRUE);
        }

        if (this.password.equals(password)) {
            return new CheckPasswordResponseDto(Boolean.TRUE);
        }

        return new CheckPasswordResponseDto(Boolean.FALSE);
    }

    public EnterGameRoomResponseDto enterUser(UUID uuid, UserInfoItem userInfoItem) {
        if (!gameRoomType.equals(GameRoomType.WAITING)) {
            throw new MultiModeException(MultiModeExceptionInfo.ALREADY_STARTED_ROOM);
        }

        if (totalUsers == MAX_ROOM_USER) {
            throw new MultiModeException(MultiModeExceptionInfo.FULL_ROOM_USER);
        }

        userInfoItems.put(uuid, userInfoItem);
        totalUsers++;

        return EnterGameRoomResponseDto.builder()
            .userInfoItems(userInfoItems.values().stream().toList())
            .gameRoomManagerNickname(this.roomManagerNickname)
            .enteredUserNickname(userInfoItems.get(uuid).getNickname())
            .build();
    }

    public EnterGameRoomDto getGameRoomInformation(UUID uuid) {
        return EnterGameRoomDto.builder()
            .messageType(MessageDtoType.ENTERUSER)
            .userInfoItems(userInfoItems.values().stream().toList())
            .gameRoomManagerNickname(this.roomManagerNickname)
            .enteredUserNickname(userInfoItems.get(uuid).getNickname())
            .build();
    }

    public void initializeGameStart() {
        this.gameRoomType = GameRoomType.GAME;
        this.playType = PlayType.ROUNDSTART;
        this.time = 5;
        this.round = 1;
    }

    public void initializeGameEnd() {
        this.gameRoomType = GameRoomType.WAITING;
        this.skipVote = 0;

        for (UserInfoItem userInfo : this.userInfoItems.values()) {
            userInfo.initializeUserInfo();
        }
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getNicknames() {
        StringBuilder nicknames = new StringBuilder();
        for(UserInfoItem userInfoItem : this.userInfoItems.values()) {
            nicknames.append(userInfoItem.getNickname()).append(SPACE);
        }

        return nicknames.toString();
    }

    public GetUserInfoItemDto getUserInfoItemDto() {
        StringBuilder nicknames = new StringBuilder();
        StringBuilder exps = new StringBuilder();
        for(UserInfoItem userInfoItem : this.userInfoItems.values()) {
            nicknames.append(userInfoItem.getNickname()).append(SPACE);
            exps.append(userInfoItem.getScore() * MULTI_MODE_EXP_WEIGHT).append(SPACE);
        }

        return GetUserInfoItemDto.builder()
            .nicknames(nicknames.toString())
            .exps(exps.toString())
            .build();
    }


    public void gameRoomUserScoreReset() {
        for (Map.Entry<UUID, UserInfoItem> entry : this.userInfoItems.entrySet()) {
            entry.getValue().initializeUserInfo();
            this.userInfoItems.put(entry.getKey(), entry.getValue());
        }
    }
}