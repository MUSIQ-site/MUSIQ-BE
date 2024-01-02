package com.a608.musiq.domain.music.dto.responseDto.v2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckAnswerResponseDto {
    private Boolean isCorrect;
    private Boolean isEnded;
    private Integer tryNum;

    public static CheckAnswerResponseDto from(Boolean isCorrect, Boolean isEnded, Integer tryNum) {
        return CheckAnswerResponseDto.builder()
                .isCorrect(isCorrect)
                .isEnded(isEnded)
                .tryNum(tryNum)
                .build();
    }
}
