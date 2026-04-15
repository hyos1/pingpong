package com.example.pingpong.service;

import com.example.pingpong.common.ClientException;
import com.example.pingpong.common.ErrorCode;
import com.example.pingpong.config.JwtUtil;
import com.example.pingpong.domain.User;
import com.example.pingpong.repository.UserRepository;
import com.example.pingpong.service.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public LoginResponse login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ClientException(ErrorCode.USER_NOT_FOUND)
        );
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ClientException(ErrorCode.INVALID_PASSWORD);
        }

        String token = jwtUtil.createToken(user.getId(), user.getEmail());
        return new LoginResponse(user.getId(), user.getUsername(), user.getEmail(), token);
    }
}