package com.a608.musiq.domain.music.dto.responseDto.v2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleRoundEndResponseDto {

    private String title;
    private String singer;
    private Integer round;
    private Integer life;
    private Boolean isGameOver;

    public static SingleRoundEndResponseDto from(String title, String singer, Integer round, Integer life,
                                                 Boolean isGameOver) {
        return SingleRoundEndResponseDto.builder()
                .title(title)
                .singer(singer)
                .round(round)
                .life(life)
                .isGameOver(isGameOver)
                .build();
    }
}
