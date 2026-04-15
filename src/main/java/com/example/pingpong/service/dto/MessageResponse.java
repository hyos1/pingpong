package com.example.pingpong.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MessageResponse {

    private Long messageId;
    private String content;
    private String senderUsername;
    private LocalDateTime sentAt;
}