package com.example.pingpong.service;

import com.example.pingpong.common.ClientException;
import com.example.pingpong.common.ErrorCode;
import com.example.pingpong.config.JwtUtil;
import com.example.pingpong.domain.RefreshToken;
import com.example.pingpong.domain.User;
import com.example.pingpong.repository.RefreshTokenRepository;
import com.example.pingpong.repository.UserRepository;
import com.example.pingpong.service.dto.LoginResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // 로그인
    @Transactional
    public LoginResponse login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ClientException(ErrorCode.USER_NOT_FOUND)
        );
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ClientException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtUtil.createToken(user.getId(), user.getEmail());
        String refreshToken = jwtUtil.createRefreshToken(user.getId());
        refreshTokenRepository.findByUserId(user.getId()).ifPresentOrElse(
                rt -> rt.updateToken(refreshToken),
                () -> refreshTokenRepository.save(RefreshToken.create(user.getId(), refreshToken)));
        return new LoginResponse(user.getId(), user.getUsername(), user.getEmail(), accessToken, refreshToken);
    }

    public LoginResponse getMe(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));
        String accessToken = jwtUtil.createToken(user.getId(), user.getEmail());
      log.info("AuthService User정보: {}", user);
      log.info("AuthService Token값: {}", accessToken);
        return new LoginResponse(user.getId(), user.getUsername(), user.getEmail(), accessToken, null);
    }

    // 엑세스 토큰 재발급
    public String reissue(String refreshToken) {
        Claims claims;
        try {
            claims = jwtUtil.extractClaims(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new ClientException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        Long userId = Long.valueOf(claims.getSubject());
        User user = userRepository.findById(userId).orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

        RefreshToken findRefreshToken = refreshTokenRepository.findByUserId(userId).orElseThrow(() -> new ClientException(ErrorCode.TOKEN_NOT_FOUND));
        if (!findRefreshToken.getToken().equals(refreshToken)) {
            throw new ClientException(ErrorCode.INVALID_TOKEN);
        }
        return jwtUtil.createToken(user.getId(), user.getEmail());
    }

    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}