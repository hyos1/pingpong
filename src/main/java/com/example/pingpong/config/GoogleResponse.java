package com.example.pingpong.config;

import lombok.Getter;

import java.util.Map;

@Getter
public class GoogleResponse {

    private final Map<String, Object> attributes;

    public GoogleResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getProvider() {
        return "google";
    }

    public String getProviderId() {
        return attributes.get("sub").toString();
    }

    public String getEmail() {
        return attributes.get("email").toString();
    }

    public String getName() {
        return attributes.get("name").toString();
    }

}