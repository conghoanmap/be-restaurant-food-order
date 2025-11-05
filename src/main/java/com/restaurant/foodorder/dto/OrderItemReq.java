package com.restaurant.foodorder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemReq {
    private String dishId;
    private String dishName;
    private double price;
    private int quantity;
}
