package com.example.pingpong.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomResponse {
    private final Long chatRoomId;
    private final String name;
    private final Long memberCount;
}