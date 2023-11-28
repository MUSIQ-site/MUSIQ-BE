package com.a608.musiq.domain.websocket.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExitGameRoomRequestDto {
    private Integer previousChannelNo;
}
