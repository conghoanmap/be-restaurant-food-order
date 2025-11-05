package com.restaurant.foodorder.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderMessage extends OrderReq implements Serializable {
    private String orderId;
    private String email;

    public OrderMessage(String orderId, OrderReq orderReq, String email) {
        super(orderReq.getReceiverName(), orderReq.getReceiverAddress(), orderReq.getReceiverPhone(),
                orderReq.getNote(), orderReq.getPaymentMethod(), orderReq.getOrderItemReqs());
        this.orderId = orderId;
        this.email = email;
    }

    @Override
    public String toString() {
        return "OrderMessage{" +
                "email='" + email + '\'' +
                ", receiverName='" + getReceiverName() + '\'' +
                ", receiverAddress='" + getReceiverAddress() + '\'' +
                ", receiverPhone='" + getReceiverPhone() + '\'' +
                ", note='" + getNote() + '\'' +
                ", paymentMethod='" + getPaymentMethod() + '\'' +
                ", orderItemReqs=" + getOrderItemReqs() +
                '}';
    }
}
