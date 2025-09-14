package com.restaurant.foodorder.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingReq {
    @NotBlank
    private String customerName;
    @Min(value = 1, message = "Số người phải lớn hơn 0")
    private int peoples;
    @NotBlank
    @Email
    private String customerEmail;
    @NotBlank
    private String customerPhone;
    @NotNull(message = "Booking date and time is required")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime bookingDateTime;
    private String notes; // Ghi chú thêm từ khách hàng
    private List<BookingItemDTO> bookingItems = new ArrayList<>(); // Danh sách món ăn trong đơn đặt bàn
}
