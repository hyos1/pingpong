package com.example.pingpong.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    public Message(String content, User sender, ChatRoom chatRoom) {
        this.content = content;
        this.sender = sender;
        this.chatRoom = chatRoom;
    }

    public static Message createMessage(String content, User sendUser, ChatRoom chatRoom) {
        return new Message(content, sendUser, chatRoom);
    }
}