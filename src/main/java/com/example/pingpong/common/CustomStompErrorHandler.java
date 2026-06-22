package com.example.pingpong.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import static com.example.pingpong.common.ErrorCode.*;

@Slf4j
@Component
public class CustomStompErrorHandler extends StompSubProtocolErrorHandler {

    public CustomStompErrorHandler() {
        super();
    }

    @Override
    public Message<byte[]> handleClientMessageProcessingError(
            Message<byte[]> clientMessage, Throwable ex
    ) {
        // 스프링 웹소켓은 예외를 MessageDeliveryException으로 감싸서 보내므로 진짜 원인 추출
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

        // ClientException이거나 그 자식인지 확인
        if (cause instanceof ClientException clientException) {
            String errorCode = clientException.getErrorCode().name();
            String errorMessage = clientException.getMessage();
            log.warn("[STOMP 에러 핸들러] Client 예외: {} ", errorCode);
            return createErrorMessage(errorCode, errorMessage);

        }

        log.error("[STOMP 에러 핸들러] 시스템 예외 발생: ", ex);
        return createErrorMessage(INTERNAL_SERVER_ERROR.name(), INTERNAL_SERVER_ERROR.getMessage());
    }

    private Message<byte[]> createErrorMessage(String errorCode, String errorMessage) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setNativeHeader("errorCode", errorCode);
//        accessor.setHeader("errorCode", errorCode);
        accessor.setMessage(errorMessage);
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }
}