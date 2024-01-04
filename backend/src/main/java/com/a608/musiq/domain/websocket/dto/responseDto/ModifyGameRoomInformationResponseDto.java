package com.a608.musiq.domain.websocket.dto.responseDto;

import lombok.Getter;

@Getter
public class ModifyGameRoomInformationResponseDto {
    private Boolean isSuccess;

    public ModifyGameRoomInformationResponseDto() {
        this.isSuccess = Boolean.TRUE;
    }
}
