package com.restaurant.foodorder.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.restaurant.foodorder.dto.BookingReq;
import com.restaurant.foodorder.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingReq bookingRequest) {
        return ResponseEntity.ok(bookingService.createBooking(bookingRequest));
    }
}
