package com.example.pingpong.domain;

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

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void updateOAuthInfo(String name, String profileImage) {
        this.username = name;
        this.profileImage = profileImage;
    }

    public static User createUser(String username, String email, String password) {
        return new User(username, email, password);
    }

    public static User createOAuthUser(String username, String email, String profileImage) {
        User user = new User(username, email, null);
        user.profileImage = profileImage;

        return user;
    }
}