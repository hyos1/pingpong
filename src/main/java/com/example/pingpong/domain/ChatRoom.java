package com.example.pingpong.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;
    private String name; //채팅방 이름

    public ChatRoom(String chatRoomName) {
        this.name = chatRoomName;
    }

    public static ChatRoom createChatRoom(String chatRoomName) {
        return new ChatRoom(chatRoomName);
    }
}