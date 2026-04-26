package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.enums.PaymentStatus;

import java.math.BigDecimal;

public interface PaymentProcessor {

    PaymentStatus initiatePayment(Order order);

    String createPayPalOrder(Long orderId);

    PayPalCheckoutStart createPayPalCheckout(BigDecimal total);

    PaymentStatus capturePayPalOrder(String paypalOrderId);

    PaymentStatus processCashOnDelivery(Order order);
}
