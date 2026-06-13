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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return chatRoomMemberRepository.findChatRoomByUserId(userId);
    }

    // 채팅방 생성
    @Transactional
    public ChatRoomResponse createChatRoom(String name, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ClientException(ErrorCode.USER_NOT_FOUND)
        );
        ChatRoom chatRoom = ChatRoom.createChatRoom(name);
        chatRoomRepository.save(chatRoom);

        chatRoomMemberRepository.save(ChatRoomMember.createChatRoomMember(chatRoom, user));
        return new ChatRoomResponse(chatRoom.getId(), chatRoom.getName(), 1L);
    }

    @Transactional
    public InviteResponse inviteUser(Long chatRoomId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ClientException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        chatRoomMemberRepository.save(ChatRoomMember.createChatRoomMember(chatRoom,user));
        return new InviteResponse(user.getId(), user.getUsername());
    }

    @Transactional
    public void deleteChatRoom(Long chatRoomId) {
        // 채팅, 메세지
        messageRepository.deleteByChatRoomId(chatRoomId);
        chatRoomMemberRepository.deleteByChatRoomId(chatRoomId);
        chatRoomRepository.deleteById(chatRoomId);
    }
}