package com.a608.musiq.domain.websocket.dto.gameMessageDto;

import com.a608.musiq.domain.websocket.data.MessageDtoType;
import com.a608.musiq.domain.websocket.domain.UserInfoItem;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GameStartPubDto {
    private MessageDtoType messageType;
    private String message;
    private List<GameRoomMemberInfo> memberInfos;

    @Builder
    public GameStartPubDto(List<GameRoomMemberInfo> memberInfos) {
        this.messageType = MessageDtoType.GAMESTART;
        this.memberInfos = memberInfos;
        this.message = "게임이 시작됩니다.";
    }
}
