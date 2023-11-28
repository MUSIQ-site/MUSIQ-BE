package com.a608.musiq.domain.ranking.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyRankResponseDto {

    private String rankNum;

    @Builder
    public MyRankResponseDto(String rankNum) {
        this.rankNum = rankNum;
    }
}
