package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.enums.PaymentStatus;
import org.springframework.stereotype.Service;

@Service
public class PaymentService implements PaymentProcessor {

    private final PaypalGateway paypalGateway;

    public PaymentService(PaypalGateway paypalGateway) {
        this.paypalGateway = paypalGateway;
    }

    @Override
    public PaymentStatus initiatePayment(Order order) {
        String result = paypalGateway.createPayment(order);
        if ("APPROVED".equals(result)) {
            return PaymentStatus.SUCCESS;
        }
        if ("FAILED".equals(result)) {
            return PaymentStatus.FAILED;
        }
        return PaymentStatus.CANCELED;
    }

    @Override
    public String createPayPalOrder(Long orderId) {
        return "ORDER-123";
    }

    public String createPaypalOrderLegacy(Long orderId) {
        return createPayPalOrder(orderId);
    }

    @Override
    public PaymentStatus capturePayPalOrder(String paypalOrderId) {
        return paypalOrderId != null && !paypalOrderId.isBlank()
                ? PaymentStatus.SUCCESS
                : PaymentStatus.FAILED;
    }

    @Override
    public PaymentStatus processCashOnDelivery(Order order) {
        return order != null ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }
}
