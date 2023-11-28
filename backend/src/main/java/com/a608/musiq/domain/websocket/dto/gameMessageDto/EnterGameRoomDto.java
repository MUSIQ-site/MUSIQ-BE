package com.a608.musiq.domain.websocket.dto.gameMessageDto;

import java.util.List;

import com.a608.musiq.domain.websocket.data.MessageDtoType;
import com.a608.musiq.domain.websocket.data.MessageType;
import com.a608.musiq.domain.websocket.domain.UserInfoItem;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EnterGameRoomDto {
	private MessageDtoType messageType;
	private List<UserInfoItem> userInfoItems;
	private String gameRoomManagerNickname;
	private String enteredUserNickname;
}
