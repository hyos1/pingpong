package com.example.pingpong.interceptor;

import com.example.pingpong.common.ClientException;
import com.example.pingpong.config.JwtAuthenticationToken;
import com.example.pingpong.config.JwtUtil;
import com.example.pingpong.enums.UserRole;
import com.example.pingpong.web.dto.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
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

import static com.example.pingpong.common.ErrorCode.*;

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

        // 첫 연결(CONNECT) 또는 메세지 전송(SEND) 둘 다 검사하도록 확장
        if (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.SEND.equals(accessor.getCommand())) {
            // Authorization헤더 배열의 여러 값 중 첫 번째 값"
            String bearerToken = accessor.getFirstNativeHeader("Authorization");

            try {
                // "Bearer "제거
                String token = jwtUtil.subStringToken(bearerToken); // 예외 발생 가능

                // JWT 만료, 변조 검사 시 관련 예외 발생 가능
                Claims claims = jwtUtil.extractClaims(token);
                log.info("STOMP Jwt Claims 파싱 성공: {}", claims);

                Long userId = Long.valueOf(claims.getSubject());
                String email = claims.get("email", String.class);
                UserRole userRole = UserRole.of(claims.get("userRole", String.class));

                AuthUser authUser = new AuthUser(userId, email, userRole);
                Authentication authentication = new JwtAuthenticationToken(authUser);
                accessor.setUser(authentication);

            } catch (ExpiredJwtException e) {
                log.info("[STOMP {} 시 JWT 만료]: userId={}", accessor.getCommand(), e.getClaims().getSubject());
                throw new ClientException(TOKEN_EXPIRED);
            } catch (ClientException e) {
                log.warn("[STOMP {} 시 인증 실패] 토큰 형식 문제 발생: {}", accessor.getCommand(), e.getMessage());
                throw e;
            } catch (SecurityException | MalformedJwtException | UnsupportedJwtException e) {
                log.error("[STOMP {} 시 JWT 검증 실패] [{}]: ", accessor.getCommand(), e.getClass().getSimpleName(), e);
                throw new ClientException(INVALID_TOKEN);
            } catch (Exception e) {
                log.error("[STOMP {} 시 인증 실패] 알 수 없는 시스템 오류 발생: ", accessor.getCommand(), e);
                throw new ClientException(INTERNAL_SERVER_ERROR);
            }
        }

        return message;
    }
}