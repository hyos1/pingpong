package com.example.pingpong.service;

import com.example.pingpong.common.ClientException;
import com.example.pingpong.common.ErrorCode;
import com.example.pingpong.domain.ChatRoom;
import com.example.pingpong.domain.Message;
import com.example.pingpong.domain.User;
import com.example.pingpong.repository.ChatRoomRepository;
import com.example.pingpong.repository.MessageRepository;
import com.example.pingpong.repository.UserRepository;
import com.example.pingpong.service.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    public List<MessageResponse> getChatRoomMessage(Long chatRoomId) {
        return messageRepository.findByChatRoomId(chatRoomId);
    }

    @Transactional
    public MessageResponse saveMessage(Long chatRoomId, Long senderId, String content) {
        User sender = userRepository.findById(senderId).orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ClientException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        Message message = Message.createMessage(content, sender, chatRoom);
        messageRepository.save(message);
        return new MessageResponse(message.getId(), message.getContent(), message.getSender().getUsername(), message.getCreatedAt());
    }
}