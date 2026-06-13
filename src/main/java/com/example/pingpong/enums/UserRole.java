package com.example.pingpong.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public enum UserRole {

    USER(Authority.USER),
    ADMIN(Authority.ADMIN);

    private final String userRole;

    public String getAuthority() {
        return this.userRole; // ROLE_USER or ROLE_ADMIN 반환
    }

    public static UserRole of(String role) {
        log.info("role: {}", role);
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 UserRole"));
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}