package com.example.pingpong.web.controller;

import com.example.pingpong.common.ApiResponse;
import com.example.pingpong.service.UserService;
import com.example.pingpong.web.dto.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> createUser(@RequestBody SignupRequestDto request) {
        userService.createUser(request.getUsername(), request.getEmail(), request.getPassword());
        ApiResponse<Void> ok = ApiResponse.ok("회원가입이 완료되었습니다.", null);
        return ResponseEntity.ok(ok);
    }
}