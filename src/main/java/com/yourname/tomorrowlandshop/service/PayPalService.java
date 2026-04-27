package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.exception.PaymentFailedException;

import java.math.BigDecimal;

public interface PayPalService {
    String createOrder(BigDecimal amount) throws PaymentFailedException;

    String captureOrder(String paypalOrderId) throws PaymentFailedException;
}
