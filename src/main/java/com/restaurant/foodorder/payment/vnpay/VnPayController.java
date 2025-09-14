package com.restaurant.foodorder.payment.vnpay;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.restaurant.foodorder.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payment/vnpay")
@Slf4j
public class VnPayController {
    private final VnPayService vnPayService;
    private final BookingService bookingService;

    public VnPayController(VnPayService vnPayService, BookingService bookingService) {
        this.vnPayService = vnPayService;
        this.bookingService = bookingService;
    }

    @GetMapping("/create")
    public ResponseEntity<?> createPayment(HttpServletRequest request, @RequestParam String orderId,
            @RequestParam Long amount,
            @RequestParam String orderInfo, @RequestParam String returnUrl) {
        return ResponseEntity.ok(vnPayService.createPaymentUrl(request, orderId, amount, orderInfo, returnUrl));
    }

    // Xử lý callback từ VNPay
    @GetMapping("/return")
    public String vnpayReturn(HttpServletRequest request, @RequestParam Map<String, String> allParams) {
        String vnp_ResponseCode = allParams.get("vnp_ResponseCode");
        if ("00".equals(vnp_ResponseCode)) {
            String vnp_TxnRef = allParams.get("vnp_TxnRef"); // Mã đơn hàng
            String vnp_Amount = allParams.get("vnp_Amount"); // Số tiền thanh toán
            log.info("Payment successful for orderId: {}, amount: {}", vnp_TxnRef, vnp_Amount);
            // Cập nhật trạng thái đơn hàng trong hệ thống
            bookingService.updatePaymentStatus(Long.parseLong(vnp_TxnRef), "Đã thanh toán");
            return "Thanh toán thành công!";
        } else {
            log.error("Payment failed with response code: {}", vnp_ResponseCode);
            return "Thanh toán thất bại, mã lỗi: " + vnp_ResponseCode;
        }
    }
}
