package com.a608.musiq.domain.websocket.dto.responseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameStartResponseDto {
	private int multiModeCreateGameRoomLogId;
}
