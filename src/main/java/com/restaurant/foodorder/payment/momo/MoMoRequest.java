package com.restaurant.foodorder.payment.momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoMoRequest {
    private String accessKey;
    private String partnerCode;
    private String requestType;
    private String notifyUrl;
    private String returnUrl;
    private String orderId;
    private long amount;
    private String orderInfo;
    private String requestId;
    private String extraData;
}
