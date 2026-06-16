package com.example.pingpong.oauth2;

import com.example.pingpong.config.JwtUtil;
import com.example.pingpong.enums.UserRole;
import com.example.pingpong.oauth2.dto.CustomOAuth2User;
import com.example.pingpong.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String providerUserId = oAuth2User.getProviderUserId();

        Long userId = oAuth2User.getUserId();
        String email = oAuth2User.getEmail();
        String name = oAuth2User.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String accessToken = jwtUtil.createToken(userId, email, UserRole.of(role));
        String refreshToken = jwtUtil.createRefreshToken(userId);

        log.info("인증 유저: userId={}, email={}, role={}", userId, email, role);
        refreshTokenService.save(userId, refreshToken);

        String encodedUsername = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String redirectUrl = String.format(
                "http://localhost:5173/oauth/callback?token=%s&refreshToken=%s&userId=%s&username=%s",
                accessToken,
                refreshToken,
                userId,
                encodedUsername
        );

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}