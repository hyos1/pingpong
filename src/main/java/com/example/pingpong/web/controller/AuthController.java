package com.example.pingpong.web.controller;

import com.example.pingpong.common.ApiResponse;
import com.example.pingpong.service.AuthService;
import com.example.pingpong.service.dto.LoginResponse;
import com.example.pingpong.web.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request.getEmail(), request.getPassword());
        ApiResponse<LoginResponse> apiResponse = ApiResponse.ok("로그인 성공", loginResponse);
        return ResponseEntity.ok(apiResponse);
    }
}