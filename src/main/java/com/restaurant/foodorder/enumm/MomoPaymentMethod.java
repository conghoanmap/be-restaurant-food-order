package com.restaurant.foodorder.enumm;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MomoPaymentMethod {
    QR_CODE("QR_CODE", "captureWallet"),
    ATM("ATM", "payWithATM"),
    CC("CC", "payWithCC");

    private final String code;
    private final String description;

    MomoPaymentMethod(String code, String description) {
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
