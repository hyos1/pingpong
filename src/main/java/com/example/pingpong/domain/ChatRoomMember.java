package com.example.pingpong.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomMember extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    public ChatRoomMember(User user, ChatRoom chatRoom) {
        this.user = user;
        this.chatRoom = chatRoom;
    }

    public static ChatRoomMember createChatRoomMember(User user, ChatRoom chatRoom) {
        return new ChatRoomMember(user, chatRoom);
    }
}