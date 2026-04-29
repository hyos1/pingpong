package com.example.pingpong.service;

import com.example.pingpong.config.GoogleResponse;
import com.example.pingpong.config.JwtUtil;
import com.example.pingpong.domain.User;
import com.example.pingpong.oauth2.CustomOAuth2User;
import com.example.pingpong.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("registrationId: {}", registrationId);
        if (!registrationId.equals("google")) {
            throw new OAuth2AuthenticationException("소셜 로그인은 Google만 가능합니다.");
        }

        // 사용자 정보 담긴 인터페이스
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User: {}", oAuth2User);
        // 유저 정보 가져오기
        Map<String, Object> attributes = oAuth2User.getAttributes();
        for (String key : attributes.keySet()) {
            log.info("key: {} , value: {}", key, attributes.get(key));
        }

        String name = attributes.get("name").toString();
        String email = attributes.get("email").toString();
        String picture = attributes.get("picture").toString();

        // response가 필요가 있나?
//        GoogleResponse googleResponse = new GoogleResponse(attributes);
//        String name = googleResponse.getName();
//        String email = googleResponse.getEmail();
//        String picture = googleResponse.getPicture(); // 구글 profile 안에 picture 있음

        userRepository.findByEmail(email).ifPresentOrElse(
                user -> user.updateOAuthInfo(name, picture),
                () -> userRepository.save(User.createOAuthUser(name, email, picture))
        );

        return oAuth2User;
    }
}