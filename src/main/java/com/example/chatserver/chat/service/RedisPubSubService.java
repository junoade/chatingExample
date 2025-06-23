package com.example.chatserver.chat.service;

import com.example.chatserver.chat.dto.ChatMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisPubSubService implements MessageListener {

    private final StringRedisTemplate stringRedisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public RedisPubSubService(@Qualifier("chatRedisPub") StringRedisTemplate stringRedisTemplate, SimpMessagingTemplate messagingTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    public void publish(String channel, String message) {
        stringRedisTemplate.convertAndSend(channel, message);
    }

    // 보통 pattern에는 topic의 이름의 패턴이 담김
    // 이 패턴을 기반으로 다이나믹한 코딩이 가능하다
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // StompController 참고
        String payload = new String(message.getBody()); // byte[] -> String
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ChatMessageDto chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);
            messagingTemplate.convertAndSend("/topic/" + chatMessageDto.getRoomId() , chatMessageDto);
        } catch (JsonProcessingException e) {
            // TODO 로깅 + 적절한 오류 응답 처리 또는 에러 채널로 메시지 전송 방식으로 처리
            throw new RuntimeException(e);
        }

    }
}
