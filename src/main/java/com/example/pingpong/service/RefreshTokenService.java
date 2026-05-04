package com.example.pingpong.service;

import com.example.pingpong.domain.RefreshToken;
import com.example.pingpong.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // OAuth로 매 로그인마다 "Refresh Token"도 갱신
    @Transactional
    public void save(Long userId, String refreshToken) {
        refreshTokenRepository.findByUserId(userId).ifPresentOrElse(
                rt -> rt.updateToken(refreshToken),
                () -> refreshTokenRepository.save(RefreshToken.create(userId, refreshToken))
        );
    }
}