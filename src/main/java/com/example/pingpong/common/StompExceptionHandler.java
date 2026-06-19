package com.example.pingpong.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class StompExceptionHandler {
    private final SimpMessagingTemplate template;

    @MessageExceptionHandler(ClientException.class)
    public void handleClientException(ClientException e, Principal principal) {
        log.warn("[STOMP 예외] {}", e.getMessage());
        template.convertAndSendToUser(
                principal.getName(), // 유저 ID로 등록해 둠.
                "/sub/errors",
                ApiResponse.<Void>fail(e.getMessage())
        );
    }

    @MessageExceptionHandler(Exception.class)
    public void handleException(Exception e, Principal principal) {
        log.error("[STOMP 알 수 없는 예외]: ", e);
        template.convertAndSendToUser(
                principal.getName(),
                "/sub/errors",
                ApiResponse.<Void>fail(e.getMessage())
        );
    }
}