package com.a608.musiq.domain.websocket.data;

import lombok.Getter;

@Getter
public enum GameRoomInformation {
    MINIMUM_TITLE_LENGTH(1),
    MAXIMUM_TITLE_LENGTH(18),
    QUIZ_AMOUNT_3(3),
    QUIZ_AMOUNT_10(10),
    QUIZ_AMOUNT_20(20),
    QUIZ_AMOUNT_30(30),
    MINIMUM_MAX_USER_NUMBER(1),
    MAXIMUM_MAX_USER_NUMBER(10);

    private int value;

    GameRoomInformation(int value) {
        this.value = value;
    }
}
