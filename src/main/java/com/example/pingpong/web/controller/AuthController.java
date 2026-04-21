package com.example.pingpong.web.controller;

import com.example.pingpong.common.ApiResponse;
import com.example.pingpong.service.AuthService;
import com.example.pingpong.service.dto.LoginResponse;
import com.example.pingpong.web.dto.AuthUser;
import com.example.pingpong.web.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<LoginResponse>> getMe(@AuthenticationPrincipal AuthUser authUser) {
        LoginResponse response = authService.getMe(authUser.getUserId());
        ApiResponse<LoginResponse> apiResponse = ApiResponse.ok(response);
        return ResponseEntity.ok(apiResponse);
    }

    // JWT 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request.getEmail(), request.getPassword());
        ApiResponse<LoginResponse> apiResponse = ApiResponse.ok("로그인 성공", loginResponse);
        return ResponseEntity.ok(apiResponse);
    }
}