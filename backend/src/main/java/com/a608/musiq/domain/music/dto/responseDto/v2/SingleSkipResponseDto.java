package com.a608.musiq.domain.music.dto.responseDto.v2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleSkipResponseDto {
    private Boolean isSkipped;

    public static SingleSkipResponseDto of(Boolean isSkipped) {
        return SingleSkipResponseDto.builder()
                .isSkipped(isSkipped)
                .build();
    }
}
