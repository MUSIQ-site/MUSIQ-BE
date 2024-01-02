package com.a608.musiq.domain.music.dto.responseDto.v2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckPrevGameResponseDto {
    private Boolean isExist;
    private String year;
    private String difficulty;
    private Integer round;
    private Integer life;

    public static CheckPrevGameResponseDto from(Boolean isExist, String year, String difficulty, Integer round, Integer life) {
        return CheckPrevGameResponseDto.builder()
                .isExist(isExist)
                .year(year)
                .difficulty(difficulty)
                .round(round)
                .life(life)
                .build();
    }
}
