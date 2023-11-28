package com.a608.musiq.domain.music.dto.responseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddIpInLogResponseDto {
	private String userIp;

	public static AddIpInLogResponseDto of(String ip) {
		return AddIpInLogResponseDto.builder()
			.userIp(ip)
			.build();
	}
}
