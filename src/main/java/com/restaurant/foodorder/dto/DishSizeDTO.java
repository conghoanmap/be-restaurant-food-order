package com.restaurant.foodorder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishSizeDTO {
    private String name;
    private double additionalPrice;
}
