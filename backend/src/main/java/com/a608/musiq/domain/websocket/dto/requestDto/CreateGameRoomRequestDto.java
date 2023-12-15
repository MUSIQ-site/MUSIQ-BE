package com.a608.musiq.domain.websocket.dto.requestDto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CreateGameRoomRequestDto {
    private int channelNo;
    private String roomName;
    private String password;
    private String musicYear;
    private int maxUserNumber;
    private int quizAmount;
}
