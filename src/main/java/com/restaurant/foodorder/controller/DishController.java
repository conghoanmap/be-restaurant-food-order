package com.restaurant.foodorder.controller;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.restaurant.foodorder.dto.DishReq;
import com.restaurant.foodorder.enumm.DishStatus;
import com.restaurant.foodorder.service.DishService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/dish")
@Slf4j
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllDishes(
            @RequestParam(required = false) List<Long> typeIds,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "ACTIVE") DishStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info(
                "Get all dishes with filters - typeIds: {}, keyword: {}, minPrice: {}, maxPrice: {}, status: {}, page: {}, size: {}, sortBy: {}, sortDir: {}",
                typeIds, keyword, minPrice, maxPrice, status, page, size, sortBy, sortDir);

        Sort.Direction direction = Sort.Direction.fromString(sortDir.toLowerCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return ResponseEntity.ok(dishService.getAllDishes(typeIds, keyword, minPrice, maxPrice, status, pageable));
    }

    // Lấy sản phẩm theo loại
    @GetMapping("/get-by-type/{typeId}")
    public ResponseEntity<?> getDishesByType(
            @PathVariable Long typeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = Sort.Direction.fromString(sortDir.toLowerCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return ResponseEntity.ok(dishService.getDishesByType(typeId, pageable));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> createDish(@Valid @RequestBody DishReq dishReq) {
        return ResponseEntity.ok(dishService.createDish(dishReq));
    }

    @GetMapping("/detail/{dishId}")
    public ResponseEntity<?> getDishDetails(@PathVariable Long dishId) {
        return ResponseEntity.ok(dishService.getDishDetail(dishId));
    }
}
