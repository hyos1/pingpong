package com.example.pingpong.repository;

import com.example.pingpong.domain.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
   @Query("select count(cm) from ChatRoomMember cm " +
            "where cm.chatRoom.id = :chatRoomId")
    int countByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}