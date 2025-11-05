package com.restaurant.foodorder.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.restaurant.foodorder.dto.APIResponse;
import com.restaurant.foodorder.dto.MessageDTO;
import com.restaurant.foodorder.model.temp_redis.Conversation;
import com.restaurant.foodorder.model.temp_redis.Message;

@Service
public class ChatService {
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String PREFIX = "chatWithAdmin:"; // key trong redis sẽ là chatWithAdmin:userId
    private final long TTL_HOURS = 2; // giỏ hàng hết hạn sau 2 giờ

    public ChatService(RedisTemplate<String, Object> redisTemplate, SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    // Lấy toàn bộ danh sách cuộc trò chuyện của người dùng với admin từ Redis
    public APIResponse<List<Conversation>> getChatHistory() {
        List<Conversation> conversations = new ArrayList<>();
        // Lấy tất cả các khóa trong Redis với prefix là chatWithAdmin:
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
        if (keys != null) {
            for (String key : keys) {
                Conversation conversation = (Conversation) redisTemplate.opsForValue().get(key);
                if (conversation != null) {
                    conversations.add(conversation);
                }
            }
        }
        return new APIResponse<>(200, "Get chat history successfully", conversations);
    }

    public void newMessage(String toUser, MessageDTO message) {
        String key = PREFIX;
        // Nếu tin nhắn được gửi đến admin, lưu cuộc trò chuyện với userId là người gửi
        if (toUser.equals("admin")) {
            key += message.getSender();
        } else {
            key += toUser;
        }
        Conversation conversation = (Conversation) redisTemplate.opsForValue().get(key);
        if (conversation == null) {
            conversation = new Conversation();
            conversation.setUserId(toUser.equals("admin") ? message.getSender() : toUser);
        }
        conversation.getMessages().add(new Message(
                message.getSender(),
                message.getContent(),
                message.getTimestamp()));
        redisTemplate.opsForValue().set(key, conversation, TTL_HOURS, TimeUnit.HOURS);
        messagingTemplate.convertAndSend("/topic/messages/" + toUser, message);
    }

    public APIResponse<String> deleteAllConversations(String userId) {
        String key = PREFIX + userId;
        redisTemplate.delete(key);
        return new APIResponse<>(200, "Deleted conversation for userId: " + userId, null);
    }
}
