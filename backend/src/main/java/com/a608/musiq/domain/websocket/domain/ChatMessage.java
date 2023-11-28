package com.a608.musiq.domain.websocket.domain;

import com.a608.musiq.domain.websocket.data.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String nickname;
    private String message;
    private MessageType messageType;

}
