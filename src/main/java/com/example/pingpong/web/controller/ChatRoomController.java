package com.example.pingpong.web.controller;

import com.example.pingpong.common.ApiResponse;
import com.example.pingpong.service.ChatRoomService;
import com.example.pingpong.service.MessageService;
import com.example.pingpong.service.dto.ChatRoomResponse;
import com.example.pingpong.web.dto.AuthUser;
import com.example.pingpong.web.dto.CreateChatRoomRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createChatRoom(
            @RequestBody CreateChatRoomRequest request,
            @AuthenticationPrincipal AuthUser authUser) {
        ChatRoomResponse response = chatRoomService.createChatRoom(request.getName(), authUser.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("채팅방이 생성되었습니다.", response));
    }

}