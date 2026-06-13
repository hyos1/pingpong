package com.example.pingpong.repository;

import com.example.pingpong.domain.ChatRoom;
import com.example.pingpong.domain.ChatRoomMember;
import com.example.pingpong.service.dto.ChatRoomResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

//    @Query("select crm.chatRoom from ChatRoomMember crm " +
//            "join fetch crm.chatRoom cr " +
//            "where crm.user.id = :userId")
//    List<ChatRoomMember> findChatRoomsByUserId(@Param("userId") Long userId);

    @Query("select cr.id as chatRoomId, cr.name as name, count(crm2.id) as memberCount " +
            "from ChatRoomMember crm1 " +
            "join crm1.chatRoom cr " +
            "join ChatRoomMember crm2 on cr.id = crm2.chatRoom.id " +
            "where crm1.user.id = :userId " +
            "group by cr.id, cr.name")
    List<ChatRoomResponse> findChatRoomByUserId(Long userId);

    @Query("select count(crm) from ChatRoomMember crm " +
            "where crm.chatRoom.id = :chatRoomId")
    int countByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Query("select count(crm) > 0 from ChatRoomMember crm " +
            "where crm.chatRoom.id = :chatRoomId " +
            "and crm.user.id = :userId")
    boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId);

    void deleteByChatRoomId(Long chatRoomId);
}