package com.restaurant.foodorder.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.restaurant.foodorder.dto.MessageDTO;
import com.restaurant.foodorder.service.ChatService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat/{toUser}") // client gửi tới /app/chat/{toUser}
    public MessageDTO send(@DestinationVariable String toUser, MessageDTO message) {
        chatService.newMessage(toUser, message);
        return message;
    }

    @GetMapping("/api/chat/get-all-conversations")
    @ResponseBody
    public ResponseEntity<?> allConversation() {
        return ResponseEntity.ok(chatService.getChatHistory());
    }

    @DeleteMapping("/api/chat/delete-conversation/{userId}")
    @ResponseBody
    public ResponseEntity<?> deleteAllConversations(@PathVariable String userId) {
        return ResponseEntity.ok(chatService.deleteAllConversations(userId));
    }
}
