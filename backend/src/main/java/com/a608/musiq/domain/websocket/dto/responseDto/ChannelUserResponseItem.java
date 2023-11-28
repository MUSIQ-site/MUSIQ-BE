package com.a608.musiq.domain.websocket.dto.responseDto;

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
public class ChannelUserResponseItem {
    private int userLevel;
    private String nickname;
    private Boolean isGaming;
}
