package com.example.pingpong.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Long UserId;
    private String username;
    private boolean alreadyJoined;
}