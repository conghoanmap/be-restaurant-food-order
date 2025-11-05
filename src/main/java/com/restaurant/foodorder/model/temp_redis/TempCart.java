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
@RedisHash("TempCart")
public class TempCart implements Serializable {
    @Id
    private String userId;
    private double totalPrice;
    private List<TempCartItem> items = new ArrayList<>();

    @TimeToLive(unit = TimeUnit.DAYS) // Thời gian sống tính bằng ngày
    private long ttl = 7; // Mặc định 7 ngày
}
