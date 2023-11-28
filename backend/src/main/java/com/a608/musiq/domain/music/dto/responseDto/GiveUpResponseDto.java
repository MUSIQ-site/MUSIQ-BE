package com.a608.musiq.domain.music.dto.responseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GiveUpResponseDto {
    private String title;
    private String singer;

    public static GiveUpResponseDto from(String title, String singer) {
        return GiveUpResponseDto.builder()
            .title(title)
            .singer(singer)
            .build();
    }
}
