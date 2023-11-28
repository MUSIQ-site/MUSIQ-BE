package com.a608.musiq.domain.websocket.dto.gameMessageDto;

import com.a608.musiq.domain.websocket.data.MessageDtoType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChatMessagePubDto {
    private MessageDtoType messageType;
    private String nickname;
    private String message;

    public static ChatMessagePubDto create(MessageDtoType messageType, String nickName, String message){

        return new ChatMessagePubDto(messageType, nickName, message);
    }
}
