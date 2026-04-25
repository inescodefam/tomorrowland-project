package com.yourname.tomorrowlandshop.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class JwtService {

    private final SecretKey key;
    private final Duration accessTtl = Duration.ofMinutes(15);
    private final Duration refreshTtl = Duration.ofDays(7);

    public JwtService(String secret) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public String generateAccessToken(String username) {
        return generateToken(username, accessTtl);
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, refreshTtl);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    private String generateToken(String username, Duration ttl) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttl)))
                .signWith(key)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
