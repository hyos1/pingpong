package com.example.pingpong.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:5173") //웹소켓의 CORS이므로 별개로 설정해야함
                .withSockJS(); // 웹소켓을 지원하지 않는 브라우저에서도 유사한 방식으로 실시간 통신 가능하게 해주는 JS 라이브러리
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub"); //구독의 prefix
        registry.setApplicationDestinationPrefixes("/app"); // 발행 prefix
    }
}