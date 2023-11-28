package com.a608.musiq.domain.member.dto.requestDto;

import lombok.Getter;

@Getter
public class JoinRequestDto {
	private String loginId;

	private String password;

	private String nickname;
}
