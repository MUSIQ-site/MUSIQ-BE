package com.a608.musiq.domain.websocket.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckPasswordResponseDto {
	private Boolean isCorrectPassword;
}
