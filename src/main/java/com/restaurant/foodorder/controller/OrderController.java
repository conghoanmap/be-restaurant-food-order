package com.restaurant.foodorder.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.foodorder.model.temp_redis.TempOrderItem;
import com.restaurant.foodorder.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create-temp-order")
    public ResponseEntity<?> createTempOrder(@RequestParam Long tableId, @RequestBody List<TempOrderItem> items) {
        return ResponseEntity.ok(orderService.createTempOrder(tableId, items));
    }

    @PutMapping("/update-temp-order")
    public ResponseEntity<?> updateTempOrder(@RequestParam Long tableId, @RequestBody List<TempOrderItem> items) {
        return ResponseEntity.ok(orderService.updateTempOrder(tableId, items));
    }

    @GetMapping("/get-temp-order")
    public ResponseEntity<?> getTempOrder(@RequestParam Long tableId) {
        return ResponseEntity.ok(orderService.getTempOrderByTableId(tableId));
    }

    @PostMapping("/close-and-save-order")
    public ResponseEntity<?> closeAndSaveOrder(@RequestParam Long tableId, @RequestParam String paymentMethod) {
        return ResponseEntity.ok(orderService.closeAndSaveOrder(tableId, paymentMethod));
    }
}
