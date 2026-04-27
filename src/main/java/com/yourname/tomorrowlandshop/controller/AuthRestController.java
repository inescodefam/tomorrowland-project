package com.yourname.tomorrowlandshop.controller;

import com.yourname.tomorrowlandshop.repository.UserRepository;
import com.yourname.tomorrowlandshop.service.JwtService;
import com.yourname.tomorrowlandshop.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private static final String ACCESS_COOKIE  = "access_token";
    private static final String REFRESH_COOKIE = "refresh_token";
    private static final String MESSAGE_KEY    = "message";

    private static final int ACCESS_TTL  = 15 * 60;
    private static final int REFRESH_TTL = 7 * 24 * 60 * 60;

    private static final String ERROR_KEY = "error";

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    @Value("${app.cookie.secure:true}")
    private boolean secureCookie;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> payload,
                                                     HttpServletResponse response) {
        String username = payload.get("username");
        String password = payload.get("password");
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, "Username and password required"));
        }
        return userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .map(u -> {
                    String accessToken = jwtService.generateAccessToken(u.getUsername());
                    String refreshToken = jwtService.generateRefreshToken(u.getUsername());
                    refreshTokenService.persist(
                            jwtService.extractTokenId(refreshToken),
                            u,
                            jwtService.extractExpiration(refreshToken)
                    );
                    setCookie(response, ACCESS_COOKIE, accessToken, ACCESS_TTL);
                    setCookie(response, REFRESH_COOKIE, refreshToken, REFRESH_TTL);
                    return ResponseEntity.ok(Map.of(MESSAGE_KEY, "Login successful"));
                })
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of(ERROR_KEY, "Invalid credentials")));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request,
                                                       HttpServletResponse response) {
        String refreshToken = readCookie(request, REFRESH_COOKIE);
        if (refreshToken == null || !jwtService.validateToken(refreshToken) || jwtService.isTokenExpired(refreshToken)) {
            clearAuthCookies(response);
            return ResponseEntity.status(401).body(Map.of(ERROR_KEY, "Invalid refresh token"));
        }
        String oldTokenId = jwtService.extractTokenId(refreshToken);
        String username = jwtService.extractUsername(refreshToken);
        if (!refreshTokenService.isActive(oldTokenId, username)) {
            clearAuthCookies(response);
            return ResponseEntity.status(401).body(Map.of(ERROR_KEY, "Invalid refresh token"));
        }
        String accessToken = jwtService.generateAccessToken(username);
        String rotatedRefreshToken = jwtService.generateRefreshToken(username);
        String rotatedTokenId = jwtService.extractTokenId(rotatedRefreshToken);

        var userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            refreshTokenService.revoke(oldTokenId);
            clearAuthCookies(response);
            return ResponseEntity.status(401).body(Map.of(ERROR_KEY, "Invalid refresh token"));
        }
        refreshTokenService.revoke(oldTokenId);
        refreshTokenService.persist(rotatedTokenId, userOptional.get(), jwtService.extractExpiration(rotatedRefreshToken));

        setCookie(response, ACCESS_COOKIE, accessToken, ACCESS_TTL);
        setCookie(response, REFRESH_COOKIE, rotatedRefreshToken, REFRESH_TTL);
        return ResponseEntity.ok(Map.of(MESSAGE_KEY, "Token refreshed"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = readCookie(request, REFRESH_COOKIE);
        if (refreshToken != null && jwtService.validateToken(refreshToken)) {
            refreshTokenService.revoke(jwtService.extractTokenId(refreshToken));
        }
        clearAuthCookies(response);
        return ResponseEntity.ok(Map.of(MESSAGE_KEY, "Logged out"));
    }

    private void clearAuthCookies(HttpServletResponse response) {
        setCookie(response, ACCESS_COOKIE, "", 0);
        setCookie(response, REFRESH_COOKIE, "", 0);
    }

    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private static String readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
