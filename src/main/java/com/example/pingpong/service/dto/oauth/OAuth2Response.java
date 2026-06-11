package com.example.pingpong.service.dto.oauth;

public interface OAuth2Response {

    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();
    String getPicture();
}