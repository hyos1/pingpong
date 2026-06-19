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
import org.springframework.messaging.support.MessageBuilder;
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

        // 첫 연결(CONNECT)일 때만 실행
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
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
                log.info("[STOMP JWT 만료]: userId={}", e.getClaims().getSubject());
                return createErrorMessage(accessor, TOKEN_EXPIRED.name(), TOKEN_EXPIRED.getMessage());
            } catch (ClientException e) {
                log.warn("[STOMP 인증 실패] 토큰 형식 문제 발생: {}", e.getMessage());
                return createErrorMessage(accessor, e.getErrorCode().name(), e.getMessage());
            } catch (SecurityException | MalformedJwtException | UnsupportedJwtException e) {
                log.error("[STOMP JWT 검증 실패] [{}]: ", e.getClass().getSimpleName(), e);
                return createErrorMessage(accessor, INVALID_TOKEN.name(), INVALID_TOKEN.getMessage());
            } catch (Exception e) {
                log.error("[STOMP 인증 실패] 알 수 없는 시스템 오류 발생: ", e);
                return createErrorMessage(accessor, INTERNAL_SERVER_ERROR.name(), INVALID_TOKEN.getMessage());
            }
        }
        return message;
    }

    private Message<byte[]> createErrorMessage(StompHeaderAccessor accessor, String errorCode, String message) {
        StompHeaderAccessor errorAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
        errorAccessor.setMessage(message);
        errorAccessor.setHeader("errorCode", errorCode);
        errorAccessor.setSessionId(accessor.getSessionId());
        errorAccessor.setSessionAttributes(accessor.getSessionAttributes());
        return MessageBuilder.createMessage(new byte[0], errorAccessor.getMessageHeaders());
    }
}