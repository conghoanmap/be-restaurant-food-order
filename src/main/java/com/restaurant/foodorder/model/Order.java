package com.restaurant.foodorder.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.restaurant.foodorder.auth.model.AppUser;
import com.restaurant.foodorder.enumm.OrderStatus;
import com.restaurant.foodorder.enumm.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;
    private String note;
    private double totalPrice;
    @Enumerated(EnumType.STRING) // Lưu enum dưới dạng chuỗi
    private PaymentMethod paymentMethod;
    private OrderStatus status = OrderStatus.PENDING;
    private boolean isPaid = false;
    private LocalDateTime orderDate = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private AppUser appUser;
}
