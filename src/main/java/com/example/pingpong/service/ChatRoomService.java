package com.example.pingpong.service;

import com.example.pingpong.common.ClientException;
import com.example.pingpong.common.ErrorCode;
import com.example.pingpong.domain.ChatRoom;
import com.example.pingpong.domain.ChatRoomMember;
import com.example.pingpong.domain.User;
import com.example.pingpong.repository.ChatRoomMemberRepository;
import com.example.pingpong.repository.UserRepository;
import com.example.pingpong.service.dto.ChatRoomResponse;
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
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public List<ChatRoomResponse> getChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomMemberRepository.findChatRoomsByUserId(userId);
        return chatRooms.stream().map(c -> new ChatRoomResponse(
                c.getId(), c.getName(), chatRoomMemberRepository.countByChatRoomId(c.getId())
        )).collect(Collectors.toList());
    }

    @Transactional
    public ChatRoomResponse createChatRoom(String name, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ClientException(ErrorCode.USER_NOT_FOUND));
        ChatRoom chatRoom = ChatRoom.createChatRoom(name);
        ChatRoomMember chatRoomMember = ChatRoomMember.createChatRoomMember(user, chatRoom);
        chatRoomMemberRepository.save(chatRoomMember);

        int count = chatRoomMemberRepository.countByChatRoomId(chatRoom.getId());
        return new ChatRoomResponse(chatRoom.getId(), chatRoom.getName(), count);
    }
}