package com.example.chatserver.chat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class StompController {

    @MessageMapping("/{roomId}") // 클라이언트에서 특정 publish/roomid 형태로 메시지 발행시 MessageMapping로 메세지 라우팅
    @SendTo("/topic/{roomId}") // 해당 roomId 에 메시지를 발행하며 구독중인 클라이언트에게 메시지 전송
    // @DestinationVariable : @MessageMapping 과 함께 쓰이며, 정의된 WebSocket Controller 내에서만 사용
    public String sendMessage(@DestinationVariable Long roomId, @Payload String message) {
        log.info("Sending message: {}", message);
        return message;
    }
}
