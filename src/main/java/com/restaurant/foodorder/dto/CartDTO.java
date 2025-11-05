package com.restaurant.foodorder.dto;

import java.util.ArrayList;
import java.util.List;

import com.restaurant.foodorder.model.temp_redis.TempCartItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private List<TempCartItem> items = new ArrayList<>();
}
