package com.example.pingpong.web.controller;

import com.example.pingpong.common.ApiResponse;
import com.example.pingpong.service.UserService;
import com.example.pingpong.service.dto.UserResponse;
import com.example.pingpong.web.dto.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> createUser(@RequestBody SignupRequestDto request) {
        userService.createUser(request.getUsername(), request.getEmail(), request.getPassword());
        ApiResponse<Void> ok = ApiResponse.ok("회원가입이 완료되었습니다.", null);
        return ResponseEntity.ok(ok);
    }

    // 회원 검색 - 채팅방 구독했는지 여부 포함
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserResponse>> findUser(@RequestParam Long chatRoomId, @RequestParam String username) {
        UserResponse userResponse = userService.findUserByUsername(chatRoomId, username);
        ApiResponse<UserResponse> apiResponse = ApiResponse.ok(userResponse);
        return ResponseEntity.ok(apiResponse);
    }
}