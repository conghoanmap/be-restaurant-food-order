package com.restaurant.foodorder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodDetail {
    private Long id;
    private String name;
    private double price;
    private String image;
    private String description;
    private boolean available;
    private String foodTypeName;
}
