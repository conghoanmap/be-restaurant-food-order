package com.restaurant.foodorder.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.foodorder.service.DishTypeService;

@RestController
@RequestMapping("/api/dish-type")
public class DishTypeController {
    private final DishTypeService dishTypeService;

    public DishTypeController(DishTypeService dishTypeService) {
        this.dishTypeService = dishTypeService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllDishTypes() {
        return ResponseEntity.ok(dishTypeService.getAllDishTypes());
    }
}
