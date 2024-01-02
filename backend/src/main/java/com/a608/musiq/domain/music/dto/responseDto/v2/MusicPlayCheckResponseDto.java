package com.a608.musiq.domain.music.dto.responseDto.v2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MusicPlayCheckResponseDto {
    private Boolean isAvail;
    private Integer listenNum;

    public static MusicPlayCheckResponseDto from(Boolean isAvail, Integer listenNum) {
        return MusicPlayCheckResponseDto.builder()
                .isAvail(isAvail)
                .listenNum(listenNum)
                .build();
    }
}
