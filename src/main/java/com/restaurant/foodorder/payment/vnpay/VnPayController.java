package com.restaurant.foodorder.payment.vnpay;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.foodorder.service.OrderService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payment/vnpay")
@Slf4j
public class VnPayController {
    private final VnPayService vnPayService;
    private final OrderService orderService;

    public VnPayController(VnPayService vnPayService, OrderService orderService) {
        this.vnPayService = vnPayService;
        this.orderService = orderService;
    }

    @GetMapping("/create")
    public ResponseEntity<?> createPayment(HttpServletRequest request, @RequestParam String orderId,
            @RequestParam Long amount,
            @RequestParam String orderInfo, @RequestParam String returnUrl) {
        return ResponseEntity.ok(vnPayService.createPaymentUrl(request, orderId, amount, orderInfo, returnUrl));
    }

    // Xử lý callback từ VNPay
    @GetMapping("/return")
    public ResponseEntity<?> vnpayReturn(HttpServletRequest request, @RequestParam Map<String, String> allParams) {
        String vnp_ResponseCode = allParams.get("vnp_ResponseCode");
        if ("00".equals(vnp_ResponseCode)) {
            String vnp_TxnRef = allParams.get("vnp_TxnRef"); // Mã đơn hàng
            return ResponseEntity.ok(orderService.updatePaymentStatus(vnp_TxnRef, true));
        } else {
            return ResponseEntity.ok("Thanh toán thất bại, mã lỗi: " + vnp_ResponseCode);
        }
    }
}
