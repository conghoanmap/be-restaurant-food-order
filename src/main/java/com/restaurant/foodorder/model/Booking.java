package com.restaurant.foodorder.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.restaurant.foodorder.enumm.BookingStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings") // Bảng dùng để lưu thông tin đặt bàn trước
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private LocalDateTime createdAt = LocalDateTime.now(); // Thời gian tạo đơn đặt bàn

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus bookingStatus = BookingStatus.PENDING; // Trạng thái đặt bàn

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingItem> bookingItems = new ArrayList<>(); // Danh sách món ăn trong đơn đặt bàn
}
