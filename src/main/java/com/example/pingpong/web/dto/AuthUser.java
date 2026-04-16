package com.example.pingpong.web.dto;

import com.example.pingpong.enums.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

@Getter
public class AuthUser implements Principal {

    private final Long userId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthUser(Long userId, String email, UserRole userRole) {
        this.userId = userId;
        this.email = email;
        this.authorities = List.of(new SimpleGrantedAuthority(userRole.name()));
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }
}