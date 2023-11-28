package com.a608.musiq.domain.websocket.dto.gameMessageDto;

import com.a608.musiq.domain.websocket.data.MessageDtoType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BeforeAnswerCorrectDto {

    private MessageDtoType messageType;
    private String winner;
    private String title;
    private String singer;
    private String singerHint;
    private String initialHint;
    private Integer skipVote;
    private List<GameRoomMemberInfo> memberInfos;

    public static BeforeAnswerCorrectDto create(MessageDtoType messageType, String winner,
        String title, String singer, Integer skipVote,List<GameRoomMemberInfo> memberInfos ){
        String singerHint = "";
        String initialHint = "";
        return new BeforeAnswerCorrectDto(messageType, winner, title, singer, singerHint,
            initialHint, skipVote, memberInfos);
    }

}
