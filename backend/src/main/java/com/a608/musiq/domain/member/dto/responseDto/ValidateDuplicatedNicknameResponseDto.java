package com.a608.musiq.domain.member.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidateDuplicatedNicknameResponseDto {
	private boolean isValid;
}
