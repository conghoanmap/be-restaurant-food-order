package com.restaurant.foodorder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodRes {
    private Long id;
    private String name;
    private double price;
    private String image;
    private String description;
    private boolean available;
}
