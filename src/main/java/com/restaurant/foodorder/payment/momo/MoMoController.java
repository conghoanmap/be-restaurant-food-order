package com.restaurant.foodorder.payment.momo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.restaurant.foodorder.service.BookingService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payment/momo")
@Slf4j
public class MoMoController {
    private final MoMoService moMoService;
    private final BookingService bookingService;

    public MoMoController(MoMoService moMoService, BookingService bookingService) {
        this.moMoService = moMoService;
        this.bookingService = bookingService;
    }

    @GetMapping("/create")
    public ResponseEntity<?> createPayment(@RequestParam String orderId, @RequestParam Long amount,
            @RequestParam String orderInfo, @RequestParam String returnUrl, @RequestParam String paymentMethod) {
        String newOrderId = "RFOBOOKING" + orderId;
        return ResponseEntity.ok(moMoService.createPayment(newOrderId, amount, orderInfo, returnUrl, paymentMethod));
    }

    @PostMapping("/return")
    public String moMoReturn(@RequestBody MoMoReturn moMoReturn) {
        if (moMoReturn.getResultCode() == 0) {
            // Cắt prefix "RFOBOOKING" khỏi orderId
            String originalOrderId = moMoReturn.getOrderId().replace("RFOBOOKING", "");
            // Xử lý logic sau khi thanh toán thành công
            log.info("Payment successful for orderId: {}", moMoReturn.getOrderId());
            // Cập nhật trạng thái đơn hàng trong hệ thống
            bookingService.updatePaymentStatus(Long.parseLong(originalOrderId), "Đã thanh toán");
        } else {
            log.error("Payment for orderId: {} failed with error code: {}", moMoReturn.getOrderId(),
                    moMoReturn.getResultCode());
        }
        return "Redirect received from MoMo!";
    }
}
