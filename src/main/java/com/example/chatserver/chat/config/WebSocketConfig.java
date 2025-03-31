package com.example.chatserver.chat.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@AllArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final SimpleWebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // connect url 로 websocket 연결 요청 들어오면,
        // 요청을 처리할 핸들러 객체를 등록
        registry.addHandler(webSocketHandler, "/connect")
                // 웹소켓에 대한 cors 예외
                // Security Config 에서의 cors 예외는 http 요청에 대한 예외라 별도의 설정
                .setAllowedOrigins("http://localhost:3000");

    }

}
