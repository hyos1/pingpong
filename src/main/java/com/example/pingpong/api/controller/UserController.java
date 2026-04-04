package com.example.pingpong.api.controller;

import com.example.pingpong.api.dto.AddUserRequestDto;
import com.example.pingpong.domain.User;
import com.example.pingpong.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/addUser")
    public ResponseEntity<User> addUser(@RequestBody AddUserRequestDto request) {
        User user = userService.createUser(request.getName(), request.getPassword(), request.getAge());
        log.info("user={}", user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}