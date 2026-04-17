package com.example.pingpong.service;

import com.example.pingpong.common.ClientException;
import com.example.pingpong.common.ErrorCode;
import com.example.pingpong.domain.User;
import com.example.pingpong.repository.ChatRoomMemberRepository;
import com.example.pingpong.repository.UserRepository;
import com.example.pingpong.service.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    @Transactional
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

    public UserResponse findUserByUsername(Long chatRoomId, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));
        boolean alreadyJoined = chatRoomMemberRepository.existsByChatRoomIdAndUserId(chatRoomId, user.getId());
        return new UserResponse(user.getId(), user.getUsername(), alreadyJoined);
    }
}