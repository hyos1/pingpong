package com.example.pingpong.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Void>> clientExHandler(ClientException e) {
        log.warn("클라이언트 예외 발생: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse<Void> failResponse = ApiResponse.fail(errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(failResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Void>> exHandler(Exception e) {
        log.error("서버 예외 발생", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}