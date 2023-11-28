package com.a608.musiq.domain.websocket.data;

import com.a608.musiq.domain.websocket.domain.Channel;
import com.a608.musiq.domain.websocket.domain.GameRoom;
import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GameValue {

    /**
     * List index No :
     *          0 ~ 9 = 1 ~ 10채널
     *
     * UUID 사용자 식별
     *
     * Integer 구독 번호 :
     *          1 ~ 10 = 채널
     *          1001 ~ 1999 = 1번 채널 게임 방
     *          2001 ~ 2999 = 2번 채널 게임 방
     *          ...
     *          10001 ~ 10999 = 10번 채널 게임 방
     */
    private static List<Channel> gameChannels = new ArrayList<>();
    private static ConcurrentHashMap<Integer, GameRoom> gameRooms = new ConcurrentHashMap<>();

    private static final int ROOM_DIVIDE_NUMBER = 1000;
    private static final int CHANNEL_MAX_SIZE = 10;
    private static final int CHANNEL_EACH_MAX_SIZE = 100;
    private static final int CHANNEL_SYNC = 1;

    @PostConstruct
    public void initValues() {
        for(int i = 0; i < CHANNEL_MAX_SIZE; i++) gameChannels.add(new Channel());
    }

    // channelNo 채널 접속자 수 조회
    public static int getGameChannelSize(int channelNo) {
        return gameChannels.get(channelNo - CHANNEL_SYNC).getGameChannel().size();
    }

    public static int getGameChannelMaxSize() { return CHANNEL_MAX_SIZE; }

    public static int getGameChannelEachMaxSize() {
        return CHANNEL_EACH_MAX_SIZE;
    }

    // 채널 ConcurrentHashMap 가져오기
    public static ConcurrentHashMap<UUID, Integer> getGameChannel(int channelNo) {
        return gameChannels.get(channelNo - CHANNEL_SYNC).getGameChannel();
    }

    // 채널 가져오기 (1~10채널)
    public static Channel getChannel(int channelNo) {
        return gameChannels.get(channelNo - CHANNEL_SYNC);
    }

    public static ConcurrentHashMap<Integer, GameRoom> getGameRooms() {
        return gameRooms;
    }

    // 유저 채널 번호 가져오기
    public static int getChannelNo(UUID uuid, int channelNo) {
        Channel channel = gameChannels.get(channelNo - CHANNEL_SYNC);

        return channel.getChannelNo(uuid);
    }

    // 채널에 유저 추가
    public static void addUserToChannel(UUID uuid, int channelNo) {
        Channel channel = gameChannels.get(channelNo - CHANNEL_SYNC);

        channel.addUser(uuid, channelNo);
    }

    // 채널에서 유저 제거
    public static void removeUserFromChannel(UUID uuid, int channelNo) {
        Channel channel = gameChannels.get(channelNo - CHANNEL_SYNC);

        channel.removeUser(uuid);
    }

    // 채널 이동 (로비 <-> 게임룸)
    public static void moveUserFromChannel(UUID uuid, int from, int to) {
        if (from > CHANNEL_MAX_SIZE) {
            from /= ROOM_DIVIDE_NUMBER;
        }
        Channel channel = gameChannels.get(from - CHANNEL_SYNC);

        channel.addUser(uuid, to);
    }

    // 게임룸 -> 로비
    public static void exitGameRoom(UUID uuid, int gameRoomChannelNo) {
        int lobbyChannelNo = gameRoomChannelNo / ROOM_DIVIDE_NUMBER;

        moveUserFromChannel(uuid, gameRoomChannelNo, lobbyChannelNo);
    }

    // 로비 -> 게임룸
    public static void enterGameRoom(UUID uuid, int gameRoomChannelNo) {
        int lobbyChannelNo = gameRoomChannelNo / ROOM_DIVIDE_NUMBER;

        moveUserFromChannel(uuid, lobbyChannelNo, gameRoomChannelNo);
    }

    // 로비 -> 게임룸 이동 시 GameChannel에 key, value Add
    public static void addGameChannel(int gameRoomNo, GameRoom gameRoom) {
        gameRooms.put(gameRoomNo, gameRoom);
    }

    public static void deleteGameRoom(int lobbyChannelNo, int gameRoomIndex, int gameRoomNo) {
        Channel channel = gameChannels.get(lobbyChannelNo - CHANNEL_SYNC);
        channel.clearGameRoom(gameRoomIndex);

        gameRooms.remove(gameRoomNo);
    }

}