package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.domain.enums.PaymentStatus;
import com.yourname.tomorrowlandshop.service.PayPalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/orders/paypal")
@RequiredArgsConstructor
@Slf4j
public class OrderRestController {

    private final PayPalService payPalService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> create(@RequestBody Map<String, Long> payload) {
        Long cents = payload.getOrDefault("amountCents", 0L);
        try {
            String paypalOrderId = payPalService.createOrder(java.math.BigDecimal.valueOf(cents, 2));
            return ResponseEntity.ok(Map.of("paypalOrderId", paypalOrderId));
        } catch (Exception e) {
            log.error("Failed to create PayPal order for amountCents={}", cents, e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Could not create PayPal order"));
        }
    }

    @PostMapping("/capture")
    public ResponseEntity<Map<String, String>> capture(@RequestBody Map<String, String> payload) {
        String paypalOrderId = payload.getOrDefault("paypalOrderId", "");
        try {
            String status = payPalService.captureOrder(paypalOrderId);
            return ResponseEntity.ok(Map.of("status", status));
        } catch (Exception e) {
            log.error("Failed to capture PayPal order paypalOrderId={}", paypalOrderId, e);
            return ResponseEntity.internalServerError().body(Map.of("status", PaymentStatus.FAILED.name()));
        }
    }
}
