package com.restaurant.foodorder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingItemDTO {
    private Long foodId;
    private String foodName;
    private double price;
    private int quantity;
    private String notes;
}
