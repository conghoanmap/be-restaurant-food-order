package com.restaurant.foodorder.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@jakarta.persistence.Table(name = "tables")
public class Table {
    @Id
    private Long id;
    private int seats;
    private String typeTable; // Loại bàn (ví dụ: ăn tại chỗ, mang về, giao hàng,...)
}
