package com.yourname.tomorrowlandshop.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaypalGatewayConfig {

    @Bean
    PaypalGateway paypalGateway() {
        return order -> "APPROVED";
    }
}
