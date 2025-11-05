package com.restaurant.foodorder.payment.momo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.foodorder.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payment/momo")
@Slf4j
public class MoMoController {
    private final MoMoService moMoService;
    private final OrderService orderService;

    public MoMoController(MoMoService moMoService, OrderService orderService) {
        this.moMoService = moMoService;
        this.orderService = orderService;
    }

    @GetMapping("/create")
    public ResponseEntity<?> createPayment(@RequestParam String orderId, @RequestParam Long amount,
            @RequestParam String orderInfo, @RequestParam String returnUrl, @RequestParam String paymentMethod) {
        return ResponseEntity.ok(moMoService.createPayment(orderId, amount, orderInfo, returnUrl, paymentMethod));
    }

    @PostMapping("/return")
    public ResponseEntity<?> moMoReturn(@RequestBody MoMoReturn moMoReturn) {
        if (moMoReturn.getResultCode() == 0) {
            return ResponseEntity.ok(orderService.updatePaymentStatus(moMoReturn.getOrderId(), true));
        } else {
            log.error("Payment for orderId: {} failed with error code: {}", moMoReturn.getOrderId(),
                    moMoReturn.getResultCode());
        }
        return ResponseEntity.ok("Redirect received from MoMo!");
    }
}
