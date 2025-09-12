package com.restaurant.foodorder.model;

import com.restaurant.foodorder.enumm.TableStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TableStatus status = TableStatus.AVAILABLE; // Trạng thái bàn
}
