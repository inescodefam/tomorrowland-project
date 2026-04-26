package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.enums.PaymentStatus;

public interface PaymentProcessor {

    PaymentStatus initiatePayment(Order order);

    String createPayPalOrder(Long orderId);

    PaymentStatus capturePayPalOrder(String paypalOrderId);

    PaymentStatus processCashOnDelivery(Order order);
}
