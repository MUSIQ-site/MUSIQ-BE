package com.a608.musiq.domain.websocket.listener;

import com.a608.musiq.domain.websocket.data.ConnectType;
import com.a608.musiq.domain.websocket.service.GameService;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private GameService gameService;

    // 소켓 연결 요청
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web-socket connection");
        MessageHeaderAccessor accessor = NativeMessageHeaderAccessor.getAccessor(event.getMessage(),
                SimpMessageHeaderAccessor.class);
        GenericMessage<?> generic = (GenericMessage<?>) accessor.getHeader("simpConnectMessage");
        logger.info("generic = {}", generic);
        Map<String, Object> nativeHeaders = (Map<String, Object>) generic.getHeaders().get("nativeHeaders");
        logger.info("nativeHeader = {}", nativeHeaders);
        Integer channelNo = Integer.parseInt(((List<String>) nativeHeaders.get("channelNo")).get(0));
        logger.info("channelNo = {}", channelNo);
        String accessToken = ((List<String>) nativeHeaders.get("accessToken")).get(0);
        logger.info("accessToken = {}", accessToken);
        String connectType = ((List<String>) nativeHeaders.get("connectType")).get(0);
        logger.info("connectType = {}", connectType);

        if (connectType.equals(ConnectType.ENTER_LOBBY.toString())) {
            gameService.joinGameChannel(accessToken, channelNo);
        } else if (connectType.equals(ConnectType.ENTER_GAME_ROOM.toString())) {
            gameService.enterGameRoomForPublish(accessToken, channelNo);
        }
    }

    // 구독 요청
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        logger.info("Received a new web-socket subscribe");

    }

    // 연결 해제
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = headerAccessor.getSessionId();

        logger.info("[Disconnected] web-socket session id : {}", sessionId);

    }

}
