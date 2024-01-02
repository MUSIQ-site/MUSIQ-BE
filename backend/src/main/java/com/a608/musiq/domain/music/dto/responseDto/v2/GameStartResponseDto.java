package com.a608.musiq.domain.music.dto.responseDto.v2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameStartResponseDto {
    private String difficulty;
    private Integer round;
    private Integer life;
    private Integer tryNum;
    private Integer listenNum;
    private String musicUrl;

    public static GameStartResponseDto from(String difficulty, Integer round, Integer life, Integer tryNum, Integer listenNum, String musicUrl) {
        return GameStartResponseDto.builder()
                .difficulty(difficulty)
                .round(round)
                .life(life)
                .tryNum(tryNum)
                .listenNum(listenNum)
                .musicUrl(musicUrl)
                .build();
    }
}
