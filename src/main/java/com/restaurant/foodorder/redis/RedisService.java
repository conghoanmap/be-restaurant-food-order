package com.restaurant.foodorder.redis;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Lưu data với TTL
    public void save(String key, Object value, long ttlSeconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
        // Lưu data với key, value và thời gian sống (TTL) tính bằng giây
    }

    // Lấy data
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Xóa data
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public void update(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
}
