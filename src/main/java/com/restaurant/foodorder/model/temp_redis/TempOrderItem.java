package com.restaurant.foodorder.model.temp_redis;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempOrderItem implements Serializable {
    private Long foodId;
    private String foodName;
    private int quantity;
    private double price;
    private String note;
}
