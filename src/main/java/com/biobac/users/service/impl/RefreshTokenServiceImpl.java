package com.biobac.users.service.impl;

import com.biobac.users.entity.RefreshToken;
import com.biobac.users.entity.User;
import com.biobac.users.exception.NotFoundException;
import com.biobac.users.repository.RefreshTokenRepository;
import com.biobac.users.service.RefreshTokenService;
import com.biobac.users.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.refresh-token.expiration-ms}")
    private long refreshTokenExpiry;

    @Transactional
    @Override
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId())
                .orElse(new RefreshToken());

        refreshToken.setUser(user);
        refreshToken.setToken(jwtUtil.generateRefreshToken(user));
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiry / 1000));

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    @Override
    public RefreshToken verifyRefreshToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new NotFoundException("Refresh token is missing");
        }
        if (!jwtUtil.validateRefreshToken(token)) {
            throw new NotFoundException("Invalid refresh token");
        }
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Refresh token not found"));
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new NotFoundException("Refresh token has expired");
        }
        return refreshToken;
    }
}
