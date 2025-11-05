package com.restaurant.foodorder.model.temp_redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String sender; // "user" hoặc "admin"
    private String content;
    private String timestamp; // Thời gian gửi tin nhắn
}
