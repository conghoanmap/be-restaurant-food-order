package com.restaurant.foodorder.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishReq {
    private String name;
    private String description;
    private double price;
    private String image;

    private List<DishSizeDTO> dishSizes;
    private List<Long> dishTypeId;

}
