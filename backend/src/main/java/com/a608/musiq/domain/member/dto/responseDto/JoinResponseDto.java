package com.a608.musiq.domain.member.dto.responseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JoinResponseDto {
	private String nickname;

	public static JoinResponseDto of(String nickname) {
		return JoinResponseDto.builder()
			.nickname(nickname)
			.build();
	}
}
