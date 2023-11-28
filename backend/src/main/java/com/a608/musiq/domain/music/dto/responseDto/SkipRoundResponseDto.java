package com.a608.musiq.domain.music.dto.responseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SkipRoundResponseDto {
	private int round;
	private String title;
	private String singer;

	public static SkipRoundResponseDto from(int round, String title, String singer) {
		return SkipRoundResponseDto.builder()
			.round(round)
			.title(title)
			.singer(singer)
			.build();
	}
}
