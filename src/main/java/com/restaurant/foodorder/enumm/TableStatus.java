package com.restaurant.foodorder.enumm;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TableStatus {
    AVAILABLE("AVAILABLE", "Bàn trống"),
    OCCUPIED("OCCUPIED", "Bàn đã có khách"),
    RESERVED("RESERVED", "Bàn đã được đặt trước"),
    OUT_OF_SERVICE("OUT_OF_SERVICE", "Bàn không sử dụng được");

    private final String code;
    private final String description;

    TableStatus(String code, String description) {
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
