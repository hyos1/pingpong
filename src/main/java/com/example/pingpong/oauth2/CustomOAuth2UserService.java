package com.example.pingpong.oauth2;

import com.example.pingpong.domain.User;
import com.example.pingpong.enums.UserRole;
import com.example.pingpong.oauth2.dto.CustomOAuth2User;
import com.example.pingpong.oauth2.dto.UserDto;
import com.example.pingpong.repository.UserRepository;
import com.example.pingpong.oauth2.dto.GoogleResponse;
import com.example.pingpong.oauth2.dto.OAuth2Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

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

        // Access Token으로 구글에 사용자 정보 요청 후 OAuth2User로 변환
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User: {}", oAuth2User);
        // 유저 정보 가져오기
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2Response oAuth2Response = new GoogleResponse(attributes);
        String providerUserId = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        String name = oAuth2Response.getName();
        String email = oAuth2Response.getEmail();
        String picture = oAuth2Response.getPicture(); // 구글 profile 안에 picture 있음

        Optional<User> user = userRepository.findByProviderUserId(providerUserId);
        if (user.isEmpty()) {
            User savedUser = userRepository.save(User.createOAuthUser(name, email, picture, providerUserId, UserRole.USER));
            UserDto userDto = new UserDto(savedUser.getId(), savedUser.getEmail(), savedUser.getRole().name(), savedUser.getUsername(), savedUser.getProviderUserId());
            return new CustomOAuth2User(userDto);
        } else {
            User existsUser = user.get();
            existsUser.updateOAuthInfo(name, email);
            User savedUser = userRepository.save(existsUser);// @Transactional이 없으므로 더티채킹 안됨.
            UserDto userDto = new UserDto(savedUser.getId(), savedUser.getEmail(), savedUser.getRole().name(), savedUser.getUsername(), savedUser.getProviderUserId());
            return new CustomOAuth2User(userDto);
        }
    }
}