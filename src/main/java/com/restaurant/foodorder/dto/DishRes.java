package com.restaurant.foodorder.dto;

import java.util.List;

import com.restaurant.foodorder.enumm.DishStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishRes {
    private Long id;
    private String name;
    private String description;
    private double price;
    private double discount;
    private String image;
    private DishStatus status;
    private List<DishSizeDTO> dishSizes;
    private List<String> dishTypeNames;
}
