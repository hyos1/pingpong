package com.example.pingpong.service.dto;

import lombok.Getter;

@Getter
public class LoginResponse {

    private Long userId;
    private String username;
    private String email;
    private String token;
    private String refreshToken;

    public LoginResponse(Long userId, String username, String email, String token, String refreshToken) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.token = token;
        this.refreshToken = refreshToken;
    }
}