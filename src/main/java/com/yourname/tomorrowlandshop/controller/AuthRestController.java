package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final JwtService jwtService;

    public AuthRestController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> payload) {
        String username = payload.getOrDefault("username", "user");
        return ResponseEntity.ok(Map.of(
                "accessToken", jwtService.generateAccessToken(username),
                "refreshToken", jwtService.generateRefreshToken(username)
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> payload) {
        String refreshToken = payload.getOrDefault("refreshToken", "");
        if (!jwtService.validateToken(refreshToken) || jwtService.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid refresh token"));
        }
        String username = jwtService.extractUsername(refreshToken);
        return ResponseEntity.ok(Map.of("accessToken", jwtService.generateAccessToken(username)));
    }
}
