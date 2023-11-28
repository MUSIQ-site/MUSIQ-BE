package com.a608.musiq.domain.websocket.dto.responseDto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameRoomListResponseDto {
    private List<GameRoomListResponseItem> rooms;
}
