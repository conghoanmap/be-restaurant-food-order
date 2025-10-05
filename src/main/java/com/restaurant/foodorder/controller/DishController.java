package com.restaurant.foodorder.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.restaurant.foodorder.dto.DishReq;
import com.restaurant.foodorder.service.DishService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/dish")
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllDishes() {
        return ResponseEntity.ok(dishService.getAllDishes());
    }

    // Lấy sản phẩm theo loại
    @GetMapping("/get-by-type/{typeId}")
    public ResponseEntity<?> getDishesByType(@PathVariable Long typeId) {
        return ResponseEntity.ok(dishService.getDishesByType(typeId));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> createDish(@Valid @RequestBody DishReq dishReq) {
        return ResponseEntity.ok(dishService.createDish(dishReq));
    }
}
