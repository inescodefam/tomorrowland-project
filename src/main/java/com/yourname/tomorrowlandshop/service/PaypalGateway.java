package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Order;

public interface PaypalGateway {
    String createPayment(Order order);
}
