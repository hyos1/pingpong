package com.example.pingpong.config;

import com.example.pingpong.common.ApiResponse;
import com.example.pingpong.enums.UserRole;
import com.example.pingpong.web.dto.AuthUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (isWhiteList(request.getRequestURI())) {
            doFilter(request, response, filterChain);
            return;
        }

        // Authorization 헤더 값 가져오기 ex) Bearer dsfasfafe.sefsfae.sfesa
        String authorization = request.getHeader("Authorization");
        log.info("JwtFilter에 온 Token값: {}", authorization);

        // Authorization 헤더가 없거나 "Bearer "로 시작하지 않으면 필터 건너뜀
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            if (!request.getRequestURI().equals("/api/users/signup") && !request.getRequestURI().equals("/api/auth/login") && !request.getRequestURI().startsWith("/ws/info")) {
                log.info("오류 URI: {} ",request.getRequestURI());
            }
            filterChain.doFilter(request, response);
            return;
        }
        // "Bearer "를 뺀 토큰 값만 추출
        String jwt = jwtUtil.subStringToken(authorization);

        // JWT 검증 및 인증 설정
        if (!processAuthentication(jwt, request, response)) {
            return;
        }

        // 검증 성공 후 다음 필터 실행
        filterChain.doFilter(request, response);
    }

    private boolean isWhiteList(String requestURI) {
        if (requestURI.equals("/api/users/signup")
                || requestURI.equals("/api/auth/login")
                || requestURI.equals("/api/auth/refresh")
                || requestURI.startsWith("/oauth2/")
                || requestURI.startsWith("/login/oauth2/")
        ) {
            return true;
        }
        return false;
    }

    private boolean processAuthentication(String jwt, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // JWT 토큰 값에서 Claims(토큰에 담긴 정보) 추출
            Claims claims = jwtUtil.extractClaims(jwt);

            log.info("Jwt Claims: {}", claims);

            // SecurityContext에 인증 정보 없으면 세팅
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("인증정보 없어서 setAuthentication 호출?");
                setAuthentication(claims);
            }
            return true;
        } catch (ExpiredJwtException e) {
            log.info("JWT 만료: userId={}, URI={}", e.getClaims().getSubject(), request.getRequestURI());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException e) {
            log.error("JWT 검증 실패 [{}]: URI={}", e.getClass().getSimpleName(), request.getRequestURI(), e);
            sendErrorResponse(response, HttpStatus.BAD_REQUEST, "인증이 필요합니다.");
        } catch (Exception e) {
            log.error("예상치 못한 오류: URI={}", request.getRequestURI(), e);
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "요청 처리 중 오류가 발생했습니다.");
        }
        return false; // 검증 실패
    }

    private void setAuthentication(Claims claims) {
        // JWT subject에서 사용자 ID 추출
        Long userId = Long.valueOf(claims.getSubject());
        // 커스텀 Claims에서 email 추출
        String email = claims.get("email", String.class);

        // 추출한 정보로 인증된 사용자 객체 생성
        AuthUser authUser = new AuthUser(userId, email, UserRole.ROLE_USER);

        // 인증된 사용자를 넘겨서 Security가 인식할 수 있는 Authentication 객체 생성
        Authentication authentication = new JwtAuthenticationToken(authUser);
        // SecurityContext에 인증정보 저장 - 이후 @AuthenticationPrincipal로 접근 가능
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(message)));
    }
}