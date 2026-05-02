package com.example.pingpong.oauth2;

import com.example.pingpong.config.JwtAuthenticationToken;
import com.example.pingpong.config.JwtUtil;
import com.example.pingpong.domain.User;
import com.example.pingpong.enums.UserRole;
import com.example.pingpong.repository.UserRepository;
import com.example.pingpong.service.RefreshTokenService;
import com.example.pingpong.web.dto.AuthUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    // UserDto를 받게 해서 순환참조 문제 해결
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email").toString();

        // 실제 사용하고 있는 이메일이 일반 회원가입 시
        // 누군가 미리 가입한 이메일 때문에 회원가입이 안될텐데 어떻게하지.
        User user = userRepository.findByEmail(email).orElseThrow();
        String accessToken = jwtUtil.createToken(user.getId(), user.getEmail());
        String refreshToken = jwtUtil.createRefreshToken(user.getId());

        log.info("인증 유저: {}", user);
        refreshTokenService.save(user.getId(), refreshToken);

        // 프론트 콜백으로 리다이렉트 — 토큰을 쿼리파라미터로 전달
        String encodedUsername = URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8);
        String redirectUrl = String.format(
                "http://localhost:5173/oauth/callback?token=%s&refreshToken=%s&userId=%s&username=%s",
                accessToken,
                refreshToken,
                user.getId(),
                encodedUsername // 한글 이니코딩
        );

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}