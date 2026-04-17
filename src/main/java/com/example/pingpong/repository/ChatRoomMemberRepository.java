package com.example.pingpong.repository;

import com.example.pingpong.domain.ChatRoom;
import com.example.pingpong.domain.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    @Query("select cm.chatRoom from ChatRoomMember cm where cm.user.id = :userId")
    List<ChatRoom> findChatRoomsByUserId(@Param("userId") Long userId);

    @Query("select count(cm) from ChatRoomMember cm " +
            "where cm.chatRoom.id = :chatRoomId")
    int countByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Query("select count(crm) > 0 from ChatRoomMember crm " +
            "where crm.chatRoom.id = :chatRoomId " +
            "and crm.user.id = :userId")
    boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId);
}