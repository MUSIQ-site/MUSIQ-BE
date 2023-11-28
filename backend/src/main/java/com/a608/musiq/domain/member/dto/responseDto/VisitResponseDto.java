package com.a608.musiq.domain.member.dto.responseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VisitResponseDto {
	private String userIp;

	public static VisitResponseDto of(String userIp) {
		return VisitResponseDto.builder()
			.userIp(userIp)
			.build();
	}
}
