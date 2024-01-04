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

    public static SingleRoundEndResponseDto from(String title, String singer, Integer round, Integer life) {
        return SingleRoundEndResponseDto.builder()
                .title(title)
                .singer(singer)
                .round(round)
                .life(life)
                .build();
    }
}
