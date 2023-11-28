package com.a608.musiq.domain.websocket.dto.gameMessageDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameRoomMemberInfo {
    private static final double SCORE_INITIAL_NUMBER = 0;

    private String nickname;
    private Double score;

    @Builder
    public GameRoomMemberInfo(String nickName) {
        this.nickname = nickName;
        this.score = SCORE_INITIAL_NUMBER;
    }

    public static GameRoomMemberInfo create(String nickname, Double score){
        return new GameRoomMemberInfo(nickname, score);
    }
}
