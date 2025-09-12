package com.restaurant.foodorder.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.foodorder.dto.FoodReq;
import com.restaurant.foodorder.service.FoodService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/food")
public class FoodController {
    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllFoods() {
        return ResponseEntity.ok(foodService.getAllFoods());
    }

    @GetMapping("/get-by-id")
    public ResponseEntity<?> getFoodById(@RequestParam Long foodId) {
        return ResponseEntity.ok(foodService.getFoodById(foodId));
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<?> createFood(@Valid @RequestBody FoodReq foodReq) {
        return ResponseEntity.ok(foodService.createFood(foodReq));
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PutMapping("/update")
    public ResponseEntity<?> updateFood(@RequestParam Long foodId, @Valid @RequestBody FoodReq foodReq) {
        return ResponseEntity.ok(foodService.updateFood(foodId, foodReq));
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFood(@RequestParam Long foodId) {
        return ResponseEntity.ok(foodService.deleteFood(foodId));
    }
}
