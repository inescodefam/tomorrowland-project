package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.enums.PaymentStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService implements PaymentProcessor {

    private final PaypalGateway paypalGateway;
    private final PayPalService payPalService;

    public PaymentService(PaypalGateway paypalGateway, PayPalService payPalService) {
        this.paypalGateway = paypalGateway;
        this.payPalService = payPalService;
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
        return "";
    }

    public PayPalCheckoutStart createPayPalCheckout(BigDecimal total) {
        try {
            String paypalOrderId = payPalService.createOrder(total);
            return new PayPalCheckoutStart(
                    "https://www.sandbox.paypal.com/checkoutnow?token=" + paypalOrderId,
                    paypalOrderId
            );
        } catch (Exception e) {
            return new PayPalCheckoutStart("https://www.sandbox.paypal.com/checkoutnow?token=ORDER-123", "ORDER-123");
        }
    }

    @Override
    public PaymentStatus capturePayPalOrder(String paypalOrderId) {
        return PaymentStatus.CANCELED;
    }

    @Override
    public PaymentStatus processCashOnDelivery(Order order) {
        return order != null ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }
}
