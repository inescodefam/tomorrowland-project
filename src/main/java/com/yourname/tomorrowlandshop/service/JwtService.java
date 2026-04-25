package com.yourname.tomorrowlandshop.service;

import java.time.Instant;

public class JwtService {

    public JwtService(String secret) {
    }

    public String generateAccessToken(String username) {
        return "access:" + username + ":" + Instant.now().toEpochMilli();
    }

    public String generateRefreshToken(String username) {
        return "refresh:" + username + ":" + Instant.now().toEpochMilli();
    }

    public boolean validateToken(String token) {
        return token != null && !token.isBlank();
    }

    public String extractUsername(String token) {
        String[] parts = token.split(":");
        return parts.length > 1 ? parts[1] : "";
    }

    public boolean isTokenExpired(String token) {
        return false;
    }
}
