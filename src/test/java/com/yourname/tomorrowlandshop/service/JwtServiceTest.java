package com.yourname.tomorrowlandshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("test-secret-test-secret-test-secret");
    }

    @Test
    void shouldGenerateAccessAndRefreshValidateExtractAndExpire() {
        String access = jwtService.generateAccessToken("user1");
        String refresh = jwtService.generateRefreshToken("user1");

        assertThat(jwtService.validateToken(access)).isTrue();
        assertThat(jwtService.validateToken(refresh)).isTrue();
        assertThat(jwtService.extractUsername(access)).isEqualTo("user1");
        assertThat(jwtService.isTokenExpired(access)).isFalse();
    }
}
