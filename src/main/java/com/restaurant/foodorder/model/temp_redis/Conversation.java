package com.restaurant.foodorder.model.temp_redis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Conversation")
public class Conversation implements Serializable {
    @Id
    private String userId; // ID người dùng tham gia cuộc trò chuyện
    private List<Message> messages = new ArrayList<>();
    @TimeToLive(unit = TimeUnit.HOURS)
    private long ttl = 2; // Cuộc trò chuyện tồn tại trong 2 giờ
}
