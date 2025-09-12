package com.restaurant.foodorder.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    @NotBlank
    private String customerName;
    @NotBlank
    private int peoples;
    @NotBlank
    private String customerEmail;
    @NotBlank
    private String customerPhone;
    @NotBlank
    private LocalDateTime bookingDateTime;
    private String notes; // Ghi chú thêm từ khách hàng
    private List<BookingItemDTO> bookingItems = new ArrayList<>(); // Danh sách món ăn trong đơn đặt bàn
}
