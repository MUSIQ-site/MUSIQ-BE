package com.a608.musiq.domain.websocket.dto.gameMessageDto;

import com.a608.musiq.domain.websocket.data.MessageDtoType;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GameRoomPubDto {

    private MessageDtoType messageType;
    private List<GameRoomMemberInfo> memberInfos;
    private Integer roomNo;
    private String roomName;

    private String password;
    private Boolean isPrivate;
//    private UUID roomManagerUUID;
    private Integer numberOfProblems;
    private String year;
    private String roomManagerNickname;

    @Builder
    public GameRoomPubDto(List<GameRoomMemberInfo> memberInfos, Integer roomNo, String roomName,
        String password, Boolean isPrivate, Integer numberOfProblems, String year,
        String roomManagerNickname) {
        this.messageType = MessageDtoType.GOWAITING;
        this.memberInfos = memberInfos;
        this.roomNo = roomNo;
        this.roomName = roomName;
        this.password = password;
        this.isPrivate = isPrivate;
        this.numberOfProblems = numberOfProblems;
        this.year = year;
        this.roomManagerNickname = roomManagerNickname;

    }

}
