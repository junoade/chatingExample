package com.example.chatserver.chat.controller;

import com.example.chatserver.chat.service.ChatService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/room/group/create")
    public ResponseEntity<?> createGroupRoom(@RequestParam("roomName") String roomName) {
        chatService.createGroupRoom(roomName);
        return ResponseEntity.ok().build();
    }
}
