package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.domain.enums.PaymentStatus;
import com.yourname.tomorrowlandshop.service.PaymentProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/orders/paypal")
public class OrderRestController {

    private final PaymentProcessor paymentProcessor;

    public OrderRestController(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> create(@RequestBody Map<String, Long> payload) {
        Long orderId = payload.getOrDefault("orderId", 0L);
        String paypalOrderId = paymentProcessor.createPayPalOrder(orderId);
        return ResponseEntity.ok(Map.of("paypalOrderId", paypalOrderId));
    }

    @PostMapping("/capture")
    public ResponseEntity<Map<String, String>> capture(@RequestBody Map<String, String> payload) {
        String paypalOrderId = payload.getOrDefault("paypalOrderId", "");
        PaymentStatus status = paymentProcessor.capturePayPalOrder(paypalOrderId);
        return ResponseEntity.ok(Map.of("status", status.name()));
    }
}
