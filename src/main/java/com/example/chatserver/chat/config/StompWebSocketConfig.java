package com.example.chatserver.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect")
                .setAllowedOrigins("http://localhost:3000")
                // ws:// 가 아닌 http:// 엔드포인트를 사용할 수 있게
                // 프론트엔드 내 sockJS 라이브러리를 통한 요청을 허용
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // publish/n 형태로 메시지를 발행(pub)하도록 설정
        // urlPattern = "/public' 으로 메시지가 발행되면, @Controller 객체의 @MessageMapping 메서드로 라우팅 시켜줌
        registry.setApplicationDestinationPrefixes("/publish");

        // topic/n 형태로 메시지를 수신(sub)하도록 설정
        registry.enableSimpleBroker("/topic");
    }
}
