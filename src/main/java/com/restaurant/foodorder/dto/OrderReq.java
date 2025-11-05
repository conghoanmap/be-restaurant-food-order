package com.restaurant.foodorder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReq {
    @NotBlank(message = "Tên người nhận không được để trống")
    private String receiverName;
    @NotBlank(message = "Địa chỉ không được để trống")
    private String receiverAddress;
    @NotBlank(message = "Số điện thoại không được để trống")
    private String receiverPhone;
    private String note;
    private String paymentMethod;
    private List<OrderItemReq> orderItemReqs = new ArrayList<>();
}
