package com.a608.musiq.domain.music.dto.responseDto.v2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoundInfoResponseDto {
    private String difficulty;
    private Integer round;
    private Integer life;
    private Integer tryNum;
    private Integer listenNum;
    private String musicUrl;

    public static RoundInfoResponseDto from(String difficulty, Integer round, Integer life, Integer tryNum, Integer listenNum, String musicUrl) {
        return RoundInfoResponseDto.builder()
                .difficulty(difficulty)
                .round(round)
                .life(life)
                .tryNum(tryNum)
                .listenNum(listenNum)
                .musicUrl(musicUrl)
                .build();
    }
}
