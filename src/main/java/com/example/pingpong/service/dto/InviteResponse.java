package com.example.pingpong.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InviteResponse {

    private Long userId;
    private String username;
}