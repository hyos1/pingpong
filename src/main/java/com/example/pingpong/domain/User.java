package com.example.pingpong.domain;

import com.example.pingpong.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column
    private String password;
    private String profileImage;
    @Column(unique = true)
    private String providerUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void updateOAuthInfo(String name, String profileImage) {
        this.username = name;
        this.profileImage = profileImage;
    }

    public static User createUser(String username, String email, String password, UserRole userRole) {
        User user = new User(username, email, password);
        user.role = userRole;
        return user;
    }

    public static User createOAuthUser(String username, String email, String profileImage, String providerUserId, UserRole userRole) {
        User user = new User(username, email, null);
        user.profileImage = profileImage;
        user.providerUserId = providerUserId;
        user.role = userRole;
        return user;
    }
}