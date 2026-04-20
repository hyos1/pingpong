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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    // 특정 채팅방의 메세지 조회
    public List<MessageResponse> getChatRoomMessage(Long chatRoomId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Message> messagePage = messageRepository.findByChatRoomId(chatRoomId, pageRequest);
        List<Message> content = messagePage.getContent();
        return content.stream().map(m -> new MessageResponse(m.getId(), m.getContent(), m.getSender().getUsername(), m.getCreatedAt())).collect(Collectors.toList());
    }

    // 특정 채팅방에 보낸 메세지 저장
    @Transactional
    public MessageResponse saveMessage(Long chatRoomId, Long senderId, String content) {
        User sender = userRepository.findById(senderId).orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ClientException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        Message message = Message.createMessage(content, sender, chatRoom);
        messageRepository.save(message);
        return new MessageResponse(message.getId(), message.getContent(), message.getSender().getUsername(), message.getCreatedAt());
    }
}