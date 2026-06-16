package com.example.pingpong.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {
    private Long userId;
    private String email;
    private String role;
    private String name;
    private String providerUserId;
}