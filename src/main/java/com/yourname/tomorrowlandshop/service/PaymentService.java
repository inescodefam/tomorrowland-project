package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.enums.PaymentStatus;

public class PaymentService {

    private final PaypalGateway paypalGateway;

    public PaymentService(PaypalGateway paypalGateway) {
        this.paypalGateway = paypalGateway;
    }

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

    public String createPaypalOrder(Long orderId) {
        return "ORDER-" + orderId;
    }
}
