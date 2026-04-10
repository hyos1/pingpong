package com.example.pingpong.service;

import com.example.pingpong.common.ClientException;
import com.example.pingpong.common.ErrorCode;
import com.example.pingpong.domain.User;
import com.example.pingpong.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public void createUser(String name, String email, String rawPassword) {

        if (userRepository.existsByUsername(name)) {
            throw new ClientException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (userRepository.existsByEmail(email)) {
            throw new ClientException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = User.createUser(name, email, encodedPassword);
        userRepository.save(user);
    }
}