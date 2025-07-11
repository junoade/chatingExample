package com.example.chatserver.chat.controller;

import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.service.ChatService;
import com.example.chatserver.chat.service.RedisPubSubService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StompController {
    // private final SimpMessagingTemplate messagingTemplate;
    // private final SimpMessageSendingOperations temp;
    private final ChatService chatService;
    private final RedisPubSubService redisPubSubService;

    @MessageMapping("/send")
    // old : 클라이언트에서 특정 publish/roomid 형태로 메시지 발행시 MessageMapping로 메세지 라우팅
    // new : 동적 바인딩 잘 안됨
    // @DestinationVariable : @MessageMapping 과 함께 쓰이며, 정의된 WebSocket Controller 내에서만 사용
    public void sendMessage(@Header("roomId") Long roomId, @Payload ChatMessageDto dto) throws JsonProcessingException {
        log.info("Room {} message: {}", roomId, dto.getMessage());

        chatService.saveMessage(roomId, dto);

        // @SendTo("/topic/{roomId}") // 해당 roomId 에 메시지를 발행하며 구독중인 클라이언트에게 메시지 전송
        // 문자열 리터럴만 허용하고 동적경로 안된다고함
        // messagingTemplate.convertAndSend("/topic/" + roomId, dto);
        dto.setRoomId(roomId);

        ObjectMapper objectMapper = new ObjectMapper();
        String messageJson = objectMapper.writeValueAsString(dto);
        redisPubSubService.publish("chat", messageJson);
    }
}