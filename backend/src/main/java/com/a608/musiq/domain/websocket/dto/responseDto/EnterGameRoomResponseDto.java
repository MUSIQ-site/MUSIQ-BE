package com.a608.musiq.domain.websocket.dto.responseDto;

import java.util.List;

import com.a608.musiq.domain.websocket.domain.UserInfoItem;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EnterGameRoomResponseDto {
    private List<UserInfoItem> userInfoItems;
    private String gameRoomManagerNickname;
    private String enteredUserNickname;
}
