package com.a608.musiq.domain.websocket.dto.responseDto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameRoomListResponseItem {
    private int gameRoomNo;
    private String roomTitle;
    private String roomManager;
    private int currentMembers;
    private int quizAmount;
    private Boolean isPrivate;
    private Boolean isPlay;
    // List로 수정
    private List<String> years;
}
