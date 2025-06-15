package com.example.chatserver.chat.controller;

import com.example.chatserver.chat.dto.ChatRoomListResDto;
import com.example.chatserver.chat.service.ChatService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/room/group/list")
    public ResponseEntity<?> getGroupChatRooms() {
        List<ChatRoomListResDto> list = chatService.getGroupChatRooms();
        return new ResponseEntity<>(list, HttpStatus.OK);

    }
}
