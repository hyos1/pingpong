package com.example.pingpong.service;

import com.example.pingpong.domain.User;
import com.example.pingpong.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(String name, String password, int age) {
        User user = User.createUser(name, password, age);
        userRepository.save(user);
        return user;
    }
}