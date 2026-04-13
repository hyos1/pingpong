package com.example.pingpong.config;

import com.example.pingpong.web.dto.AuthUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final AuthUser authUser;

    public JwtAuthenticationToken(AuthUser authUser) {
        super(authUser.getAuthorities());
        this.authUser = authUser;
        setAuthenticated(true); // Spring Security에 사용자가 이미 인증되었음을 알려줌
    }

    // JWT 인증에선 토큰 검증 후 자격 증명이 필요하지 않으므로 null 반환
    @Override
    public Object getCredentials() {
        return null;
    }

    // 인증된 사용자를 반환 (애플리케이션 전체에서 현재 사용자의 정보에 접근하는데 사용)
    @Override
    public Object getPrincipal() {
        return authUser;
    }
}