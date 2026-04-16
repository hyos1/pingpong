package com.example.pingpong.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomResponse {
    private Long chatRoomId;
    private String name;
    private int memberCount;
}