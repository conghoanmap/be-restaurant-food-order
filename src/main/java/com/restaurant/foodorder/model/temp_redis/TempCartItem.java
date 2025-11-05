package com.restaurant.foodorder.model.temp_redis;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempCartItem implements Serializable {
    private String dishId;
    private String dishName;
    private String imageUrl;
    private int quantity;
    private double price;
}
