package com.yourname.tomorrowlandshop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtServiceConfig {

    @Bean
    JwtService jwtService(@Value("${app.jwt.secret:test-secret-test-secret-test-secret-123}") String secret) {
        return new JwtService(secret);
    }
}
