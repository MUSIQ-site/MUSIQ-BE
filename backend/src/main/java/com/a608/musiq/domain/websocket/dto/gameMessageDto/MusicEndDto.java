package com.a608.musiq.domain.websocket.dto.gameMessageDto;

import com.a608.musiq.domain.websocket.data.MessageDtoType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MusicEndDto {

    private MessageDtoType messageType;
    private Boolean musicPlay;

    @Builder
    public MusicEndDto() {

        this.messageType = MessageDtoType.MUSICEND;
        this.musicPlay = false;
    }
}
