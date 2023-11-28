package com.a608.musiq.domain.websocket.data;

public enum MessageType {
    CHAT, ANSWER, DISCONNECTED, ENTERUSER, EXITUSER, GAMESTART
    /**
     * CHAT : 유저가 채팅
     * ANSWER
     * DISCONNECTED
     * ENTERUSER : GameType이 WAITING 일때 방에 들어옴
     * QUITUSER : 유저가 방에서 나감
     * GAMESTART : 게임 스타트 이벤트 발생
     * */
}
