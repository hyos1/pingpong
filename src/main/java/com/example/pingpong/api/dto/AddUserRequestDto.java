package com.example.pingpong.api.dto;

import lombok.Getter;

@Getter
public class AddUserRequestDto {

    private String name;
    private String password;
    private int age;
}