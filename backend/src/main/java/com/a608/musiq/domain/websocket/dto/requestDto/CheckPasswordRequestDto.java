package com.a608.musiq.domain.websocket.dto.requestDto;

import lombok.Getter;

@Getter
public class CheckPasswordRequestDto {
	private int gameRoomNo;
	private String password;
}
