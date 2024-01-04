package com.a608.musiq.domain.music.dto.responseDto.v2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleGameOverResponseDto {
    private String year;
    private String difficulty;
    private Integer round;

    public static SingleGameOverResponseDto from(String year, String difficulty, Integer round) {
        return SingleGameOverResponseDto.builder()
                .year(year)
                .difficulty(difficulty)
                .round(round)
                .build();
    }
}
