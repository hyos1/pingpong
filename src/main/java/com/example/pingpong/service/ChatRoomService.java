package com.example.pingpong.service;

import com.example.pingpong.common.ClientException;
import com.example.pingpong.common.ErrorCode;
import com.example.pingpong.domain.ChatRoom;
import com.example.pingpong.domain.ChatRoomMember;
import com.example.pingpong.domain.User;
import com.example.pingpong.repository.ChatRoomMemberRepository;
import com.example.pingpong.repository.ChatRoomRepository;
import com.example.pingpong.repository.MessageRepository;
import com.example.pingpong.repository.UserRepository;
import com.example.pingpong.service.dto.ChatRoomResponse;
import com.example.pingpong.service.dto.InviteResponse;
import com.example.pingpong.web.dto.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MessageRepository messageRepository;

    // 로그인 사용자의 채팅방 목록 조회
    public List<ChatRoomResponse> getChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomMemberRepository.findChatRoomsByUserId(userId);
        return chatRooms.stream().map(c -> new ChatRoomResponse(
                c.getId(), c.getName(), chatRoomMemberRepository.countByChatRoomId(c.getId())
        )).collect(Collectors.toList());
    }

    // 채팅방 생성
    @Transactional
    public ChatRoomResponse createChatRoom(String name, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

        // 채팅방 생성
        ChatRoomMember chatRoomMember = ChatRoomMember.createChatRoomMember(user); // 유저만 세팅
        ChatRoom chatRoom = ChatRoom.createChatRoom(name);
        // 회원 초대
        chatRoom.inviteMember(chatRoomMember); // 유저 초대
        chatRoomRepository.save(chatRoom);

        int count = chatRoomMemberRepository.countByChatRoomId(chatRoom.getId());
        return new ChatRoomResponse(chatRoom.getId(), chatRoom.getName(), count);
    }

    @Transactional
    public InviteResponse inviteUser(Long chatRoomId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ClientException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        ChatRoomMember chatRoomMember = ChatRoomMember.createChatRoomMember(user);
        chatRoom.inviteMember(chatRoomMember);
        return new InviteResponse(user.getId(), user.getUsername());
    }

    @Transactional
    public void deleteChatRoom(Long chatRoomId, AuthUser authUser) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ClientException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        // 메세지 삭제 후
        messageRepository.deleteByChatRoomId(chatRoomId);
        // 채팅방 삭제
        chatRoomRepository.delete(chatRoom);
    }
}