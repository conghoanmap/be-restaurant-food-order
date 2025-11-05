package com.restaurant.foodorder.enumm;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    PENDING("PENDING", "Đang chờ xử lý"),
    CONFIRMED("CONFIRMED", "Đã xác nhận"),
    COOKING("COOKING", "Đang chế biến"),
    CANCELED("CANCELED", "Đã hủy"),
    DELIVERING("DELIVERING", "Đang giao hàng"),
    DELIVERED("DELIVERED", "Đã giao hàng");

    private final String code;
    private final String description;

    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
