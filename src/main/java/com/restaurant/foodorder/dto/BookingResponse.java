package com.restaurant.foodorder.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private int peoples; // Số lượng người
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDateTime bookingDateTime; // Ngày giờ đặt bàn
    private double totalPrice; // Tổng giá trị đơn hàng
    private double depositAmount; // Số tiền đặt cọc
    private String statusPayment; // Trạng thái thanh toán (Đã thanh toán, Chưa thanh toán)
    private String notes; // Ghi chú thêm từ khách hàng
    private LocalDateTime createdAt; // Thời gian tạo đơn đặt bàn
    private String bookingStatus; // Trạng thái đặt bàn
    private List<BookingItemDTO> bookingItems; // Danh sách món ăn trong đơn đặt bàn
}
