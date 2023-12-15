package com.a608.musiq.domain.websocket.data;

import lombok.Getter;

@Getter
public enum GameRoomUserNumber {
    MINIMUM_USER_NUMBER(1),
    MAX_USER_NUMBER(10);

    private int value;

    GameRoomUserNumber(int value) {
        this.value = value;
    }
}
