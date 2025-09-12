package com.restaurant.foodorder.enumm;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
    CASH("CASH",
            "Thanh toán tiền mặt"),
    MOMO("MOMO",
            "Thanh toán qua ví điện tử MoMo"),
    VNPAY("VNPAY",
            "Thanh toán qua ví điện tử VNPAY"),
    CREDIT_CARD("CREDIT_CARD",
            "Thanh toán bằng thẻ tín dụng"),
    ATM("ATM",
            "Thanh toán bằng thẻ ATM");

    private final String code;
    private final String description;

    PaymentMethod(String code, String description) {
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
