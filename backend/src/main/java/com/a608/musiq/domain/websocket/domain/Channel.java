package com.a608.musiq.domain.websocket.domain;

import com.a608.musiq.global.exception.exception.MultiModeException;
import com.a608.musiq.global.exception.info.MultiModeExceptionInfo;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

@Getter
public class Channel {
    private static final int MAX_ROOM_NUMBER = 100;
    private static final int LOOP_START_NUMBER = 1;

    private ConcurrentHashMap<UUID, Integer> gameChannel = new ConcurrentHashMap<>();
    private boolean[] isUsed = new boolean[100];

    // UUID 들어오면 채널 번호 리턴
    public Integer getChannelNo(UUID uuid) {
        return gameChannel.get(uuid);
    }

    // 가장 빠른 빈 방 번호 리턴
    public Integer getMinimumEmptyRoomNo() {
        for(int index = LOOP_START_NUMBER; index < MAX_ROOM_NUMBER ; index++) {
            if (!isUsed[index]) {
                return index;
            }
        }

        throw new MultiModeException(MultiModeExceptionInfo.OUT_OF_ROOM_NUMBER);
    }

    public void updateIsUsed(int index) {
        isUsed[index] = true;
    }

    public void addUser(UUID uuid, int channelNo) {
        gameChannel.put(uuid, channelNo);
    }

    public void removeUser(UUID uuid) {
        gameChannel.remove(uuid);
    }

    public void clearGameRoom(int channelNo) {
        isUsed[channelNo] = Boolean.FALSE;
    }

}
