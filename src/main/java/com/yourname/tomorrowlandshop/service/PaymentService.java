package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.enums.PaymentStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

    @Override
    public PayPalCheckoutStart createPayPalCheckout(BigDecimal total) {
        String suffix = total != null ? total.stripTrailingZeros().toPlainString().replace('.', '-') : "0";
        return new PayPalCheckoutStart("https://www.sandbox.paypal.com/checkoutnow?token=MOCK-" + suffix,
                "ORDER-123");
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
