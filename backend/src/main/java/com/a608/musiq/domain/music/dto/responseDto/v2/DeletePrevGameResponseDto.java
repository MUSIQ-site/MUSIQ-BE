package com.a608.musiq.domain.music.dto.responseDto.v2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeletePrevGameResponseDto {
    private Boolean isDeleted;

    public static DeletePrevGameResponseDto of(Boolean isDeleted) {
        return DeletePrevGameResponseDto.builder()
                .isDeleted(isDeleted)
                .build();
    }
}
