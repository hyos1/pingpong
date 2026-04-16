package com.example.pingpong.web.controller;

import com.example.pingpong.service.MessageService;
import com.example.pingpong.service.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate template;
    private final MessageService messageService;

    @MessageMapping("/chat/{chatRoomId}")
    public void sendMessage(@DestinationVariable("chatRoomId") Long chatRoomId,
                            SendMessageRequestDto request,
                            Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        log.info("보낸유저 ID:{}", userId);
        log.info("메세지 들어옴:{}", request.getContent());
        MessageResponse response = messageService.saveMessage(chatRoomId, userId, request.getContent());
        template.convertAndSend("/sub/chat/" + chatRoomId, response);
    }
}