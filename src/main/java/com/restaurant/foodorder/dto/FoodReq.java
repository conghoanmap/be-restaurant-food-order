package com.restaurant.foodorder.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodReq {
    @NotBlank(message = "Food name is required")
    private String name;
    @Min(value = 10000, message = "Food price must be greater than 10000")
    @Max(value = 50000, message = "Food price must be less than 50000")
    private double price;
    private String image;
    private String description;
    private boolean available;
    private Long foodTypeId;
}
