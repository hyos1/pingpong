package com.example.pingpong.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;
    private String name; //채팅방 이름

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomMember> chatRoomMembers = new ArrayList<>();

    public ChatRoom(String chatRoomName) {
        this.name = chatRoomName;
    }

    public static ChatRoom createChatRoom(String chatRoomName) {
        return new ChatRoom(chatRoomName);
    }

    // 연관관계 편의 메서드
    public void inviteMember(ChatRoomMember chatRoomMember) {
        this.chatRoomMembers.add(chatRoomMember);
        chatRoomMember.setChatRoom(this);
    }
}