package com.example.pingpong.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    // 지원하지 않는 메서드 요청
    @ExceptionHandler
    public ResponseEntity<ApiResponse<Void>> methodNotSupportHandler(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        log.warn(requestURI + "에서 " + method + " 메서드를 지원하지 않습니다.");
        ApiResponse<Void> apiResponse = ApiResponse.fail(method + " 메서드는 지원하지 않습니다.");
        return ResponseEntity.status(e.getStatusCode()).body(apiResponse);
    }

    // 존재하지 않는 URI 요청
    @ExceptionHandler
    public ResponseEntity<ApiResponse<Void>> noResourceFoundHandler(
            NoResourceFoundException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.warn("존재하지 않는 API 호출 - URI: [{}]", requestURI);
        ApiResponse<Void> apiResponse = ApiResponse.fail("존재하지 않는 API 경로입니다.");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(apiResponse);
    }

    // 처리하지 못한 예외 발생
    @ExceptionHandler
    public ResponseEntity<ApiResponse<Void>> exHandler(Exception e) {
        log.error("서버 예외 발생", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}