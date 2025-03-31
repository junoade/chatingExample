package com.example.chatserver.chat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// connect 로 웹소켓 연결요청이 들어왔을 때 이를 처리할 클래스
@Component
@Slf4j
public class SimpleWebSocketHandler extends TextWebSocketHandler {

    // 연결된 세션 관리
    // Thread-safe 한 Set 사용
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("세션 연결 합니다.");
        sessions.add(session);
        log.info("Connection Established : {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("receive message : {}", payload);

        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(payload));
            }
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("Connection Closed : {}", session.getId());
    }

}
