package com.a608.musiq.domain.music.dto.responseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameOverResponseDto {
	private int round;
	private double exp;

	public static GameOverResponseDto of(int round) {
		return GameOverResponseDto.builder()
			.round(round)
			.build();
	}

	public static GameOverResponseDto from(int round, double exp) {
		return GameOverResponseDto.builder()
			.round(round)
			.exp(exp)
			.build();
	}
}
