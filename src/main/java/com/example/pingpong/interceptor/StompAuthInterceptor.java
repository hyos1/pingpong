package com.example.pingpong.interceptor;

import com.example.pingpong.common.ClientException;
import com.example.pingpong.common.ErrorCode;
import com.example.pingpong.config.JwtAuthenticationToken;
import com.example.pingpong.config.JwtUtil;
import com.example.pingpong.enums.UserRole;
import com.example.pingpong.web.dto.AuthUser;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("message: {}", message);
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        // 첫 연결(CONNECT)일 때만 실행
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 헤더에서 토큰 값 추출 "Bearer adfasdfa.asdfasdf.sadf"
            String bearerToken = accessor.getFirstNativeHeader("Authorization");

            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                throw new ClientException(ErrorCode.TOKEN_NOT_FOUND);
            }
            // "Bearer "제거
            String token = jwtUtil.subStringToken(bearerToken);
            Claims claims = jwtUtil.extractClaims(token);
            Long userId = Long.valueOf(claims.getSubject());
            String email = claims.get("email", String.class);

            AuthUser authUser = new AuthUser(userId, email, UserRole.ROLE_USER);
            Authentication authentication = new JwtAuthenticationToken(authUser);

            accessor.setUser((Principal) authentication.getPrincipal());
        }
        return message;
    }
}