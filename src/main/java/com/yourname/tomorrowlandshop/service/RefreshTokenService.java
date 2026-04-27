package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.RefreshToken;
import com.yourname.tomorrowlandshop.domain.entity.User;
import com.yourname.tomorrowlandshop.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void persist(String tokenId, User user, Instant expiresAt) {
        refreshTokenRepository.save(RefreshToken.builder()
                .tokenId(tokenId)
                .user(user)
                .expiresAt(expiresAt)
                .createdAt(Instant.now())
                .build());
    }

    @Transactional(readOnly = true)
    public boolean isActive(String tokenId, String username) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByTokenId(tokenId);
        return refreshToken
                .filter(token -> token.getUser().getUsername().equals(username))
                .filter(token -> token.getExpiresAt().isAfter(Instant.now()))
                .isPresent();
    }

    @Transactional
    public void revoke(String tokenId) {
        refreshTokenRepository.deleteByTokenId(tokenId);
    }
}
