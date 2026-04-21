package com.example.pingpong.web.controller;

import com.example.pingpong.common.ApiResponse;
import com.example.pingpong.service.ChatRoomService;
import com.example.pingpong.service.MessageService;
import com.example.pingpong.service.dto.ChatRoomResponse;
import com.example.pingpong.service.dto.InviteResponse;
import com.example.pingpong.service.dto.MessageResponse;
import com.example.pingpong.web.dto.AuthUser;
import com.example.pingpong.web.dto.CreateChatRoomRequest;
import com.example.pingpong.web.dto.InviteRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;

    // 채팅방 생성
    @PostMapping
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createChatRoom(
            @RequestBody CreateChatRoomRequest request,
            @AuthenticationPrincipal AuthUser authUser) {
        ChatRoomResponse response = chatRoomService.createChatRoom(request.getName(), authUser.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("채팅방이 생성되었습니다.", response));
    }

    // 로그인 사용자의 채팅방 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getChatRooms(@AuthenticationPrincipal AuthUser authUser) {
        List<ChatRoomResponse> chatRooms = chatRoomService.getChatRooms(authUser.getUserId());
        return ResponseEntity.ok(ApiResponse.ok(chatRooms));
    }

    // 특정 채팅방의 메세지 조회
    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal AuthUser authUser) {
        log.info("채팅방 ID: {}", chatRoomId);
        List<MessageResponse> messages = messageService.getChatRoomMessage(chatRoomId, page, size);
        ApiResponse<List<MessageResponse>> apiResponse = ApiResponse.ok(messages);
        return ResponseEntity.ok(apiResponse);
    }

    // 회원 초대
    @PostMapping("/{chatRoomId}/members")
    public ResponseEntity<ApiResponse<InviteResponse>> inviteUser(@PathVariable Long chatRoomId, @RequestBody InviteRequest request) {
        InviteResponse inviteResponse = chatRoomService.inviteUser(chatRoomId, request.getUserId());
        ApiResponse<InviteResponse> apiResponse = ApiResponse.ok(inviteResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{chatRoomId}")
    public ResponseEntity<ApiResponse<Void>> deleteChatRoom(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal AuthUser authUser) {
        chatRoomService.deleteChatRoom(chatRoomId, authUser);
        ApiResponse<Void> apiResponse = ApiResponse.ok("채팅방이 삭제되었습니다.", null);
        return ResponseEntity.ok(apiResponse);
    }
}