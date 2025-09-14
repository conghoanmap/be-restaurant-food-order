package com.restaurant.foodorder.payment.momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoMoResponse {
    private String partnerCode;
    private String orderId;
    private String requestId;
    private String amount;
    private double responseTime;
    private String message;
    private int resultCode;
    private String payUrl;
    private String deeplink;
    private String qrCodeUrl;
}
