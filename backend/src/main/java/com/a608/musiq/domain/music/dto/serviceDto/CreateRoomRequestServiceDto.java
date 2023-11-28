package com.a608.musiq.domain.music.dto.serviceDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateRoomRequestServiceDto {
	private String difficulty;
	private String year;
	private String token;

	public static CreateRoomRequestServiceDto from(String difficulty, String year) {
		return CreateRoomRequestServiceDto.builder()
			.difficulty(difficulty)
			.year(year)
			.build();
	}

	public static CreateRoomRequestServiceDto from(String difficulty, String year, String token) {
		return CreateRoomRequestServiceDto.builder()
			.difficulty(difficulty)
			.year(year)
			.token(token)
			.build();
	}

}
