package com.restaurant.foodorder.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.restaurant.foodorder.dto.APIResponse;
import com.restaurant.foodorder.dto.BookingReq;
import com.restaurant.foodorder.dto.BookingRes;
import com.restaurant.foodorder.model.Booking;
import com.restaurant.foodorder.model.BookingItem;
import com.restaurant.foodorder.model.Food;
import com.restaurant.foodorder.repo.BookingItemRepo;
import com.restaurant.foodorder.repo.BookingRepo;
import com.restaurant.foodorder.repo.FoodRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingService {

    private final double DEPOSIT_RATE = 0.3; // Tỷ lệ đặt cọc 30%
    private final BookingRepo bookingRepo;
    private final FoodRepo foodRepo;

    public BookingService(BookingRepo bookingRepo, BookingItemRepo bookingItemRepo, FoodRepo foodRepo) {
        this.bookingRepo = bookingRepo;
        this.foodRepo = foodRepo;
    }

    @Transactional
    public APIResponse<BookingRes> createBooking(BookingReq bookingRequest) {
        Booking booking = new Booking();
        booking.setPeoples(bookingRequest.getPeoples());
        booking.setCustomerName(bookingRequest.getCustomerName());
        booking.setCustomerEmail(bookingRequest.getCustomerEmail());
        booking.setCustomerPhone(bookingRequest.getCustomerPhone());
        booking.setBookingDateTime(bookingRequest.getBookingDateTime());
        booking.setNotes(bookingRequest.getNotes());
        // Kiểm tra xem ngày đặt bàn có hợp lệ không(phảii trong tương lai nhiều nhất 3
        // ngày)
        if (bookingRequest.getBookingDateTime().isBefore(java.time.LocalDateTime.now())
                || bookingRequest.getBookingDateTime()
                        .isAfter(java.time.LocalDateTime.now().plusDays(3))) {
            return new APIResponse<>(400, "Invalid booking date", null);
        }
        // Kiểm tra xem đã có đơn đặt bàn nào vào thời gian đó chưa
        // ...
        // Tính tong giá trị đơn hàng
        if (!bookingRequest.getBookingItems().isEmpty()) {
            // Lưu thông tin các món ăn trong đơn đặt bàn
            List<BookingItem> bookingItems = new ArrayList<>();
            bookingRequest.getBookingItems().forEach(item -> {
                BookingItem bookingItem = new BookingItem();
                bookingItem.setQuantity(item.getQuantity());
                bookingItem.setNotes(item.getNotes());
                bookingItem.setBooking(booking);
                Food food = foodRepo.findById(item.getFoodId()).orElse(null);
                if (food != null) {
                    bookingItem.setPrice(food.getPrice());
                    item.setPrice(food.getPrice());
                    item.setFoodName(food.getName());
                    bookingItem.setFood(food);
                } else {
                    bookingItem.setPrice(item.getPrice());
                }
                bookingItems.add(bookingItem);
            });

            double totalAmount = bookingItems.stream()
                    .mapToDouble(item -> item.getQuantity() * item.getPrice())
                    .sum();
            booking.setTotalPrice(totalAmount);
            booking.setDepositAmount(totalAmount * DEPOSIT_RATE);

            booking.setBookingItems(bookingItems);
            bookingRepo.save(booking);
        }
        booking.setStatusPayment("Chưa thanh toán");
        return new APIResponse<>(200, "Booking created successfully", BookingRes.builder()
                .id(booking.getId())
                .peoples(booking.getPeoples())
                .customerName(booking.getCustomerName())
                .customerEmail(booking.getCustomerEmail())
                .customerPhone(booking.getCustomerPhone())
                .bookingDateTime(booking.getBookingDateTime())
                .totalPrice(booking.getTotalPrice())
                .depositAmount(booking.getDepositAmount())
                .statusPayment(booking.getStatusPayment())
                .notes(booking.getNotes())
                .createdAt(booking.getCreatedAt())
                .bookingItems(bookingRequest.getBookingItems())
                .bookingStatus(booking.getBookingStatus().getDescription())
                .build());
    }

    public void updatePaymentStatus(Long bookingId, String status) {
        Booking booking = bookingRepo.findById(bookingId).orElse(null);
        if (booking != null) {
            booking.setStatusPayment(status);
            bookingRepo.save(booking);
            log.info("Updated payment status for booking {}: {}", bookingId, status);
        } else {
            log.warn("Booking not found: {}", bookingId);
        }
    }
}
