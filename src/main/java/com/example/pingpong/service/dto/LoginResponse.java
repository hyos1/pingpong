package com.example.pingpong.service.dto;

import lombok.Getter;

@Getter
public class LoginResponse {

    private Long userId;
    private String username;
    private String email;

    public LoginResponse(Long userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
}