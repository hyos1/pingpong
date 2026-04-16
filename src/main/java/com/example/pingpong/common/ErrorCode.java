package com.example.pingpong.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    //Authenticate
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "토큰이 비어있습니다."),

    //User
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST,"이미 사용 중인 닉네임입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED,"이메일 또는 비밀번호가 올바르지 않습니다."),

    // ChatRoom
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND,"채팅방을 찾을 수 없습니다."),
    ALREADY_JOINED(HttpStatus.BAD_REQUEST,"이미 참여 중인 채팅방입니다."),

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}