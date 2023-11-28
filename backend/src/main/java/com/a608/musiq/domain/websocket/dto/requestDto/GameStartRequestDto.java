package com.a608.musiq.domain.websocket.dto.requestDto;

import lombok.Getter;

@Getter
public class GameStartRequestDto {
	private int multiModeCreateGameRoomLogId;
	private int gameRoomNumber;
}
