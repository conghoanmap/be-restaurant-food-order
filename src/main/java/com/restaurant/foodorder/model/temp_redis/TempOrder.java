package com.restaurant.foodorder.model.temp_redis;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("TempOrder")
public class TempOrder implements Serializable {
    @Id
    private Long tableId;
    private double totalPrice;
    private List<TempOrderItem> items;

    @TimeToLive(unit = TimeUnit.MINUTES) // Thời gian sống tính bằng phút
    private long ttl = 120; // Mặc định 120 phút
}
