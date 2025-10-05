package com.restaurant.foodorder.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dish_sizes")
public class DishSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name; // Tên kích thước (ví dụ: Small, Medium, Large)
    private double additionalPrice; // Giá thêm cho kích thước này

    @ManyToOne
    @JoinColumn(name = "dish_id")
    @JsonBackReference
    private Dish dish;
}
