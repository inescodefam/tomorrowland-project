package com.yourname.tomorrowlandshop.service;

import java.math.BigDecimal;

public interface PayPalService {
    String createOrder(BigDecimal amount) throws Exception;

    String captureOrder(String paypalOrderId) throws Exception;
}
