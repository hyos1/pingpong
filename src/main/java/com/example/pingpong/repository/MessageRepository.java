package com.example.pingpong.repository;

import com.example.pingpong.domain.Message;
import com.example.pingpong.service.dto.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 페이징 적용 전 메세지 조회 쿼리
//    @Query("select new com.example.pingpong.service.dto.MessageResponse(" +
//            "m.id, m.content, u.username, m.createdAt" +
//            ") " +
//            "from Message m " +
//            "join m.sender u " +
//            "where m.chatRoom.id = :chatRoomId " +
//            "order by m.createdAt")
//    List<MessageResponse> findByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    // 페이징 적용 메세지 조회 쿼리
    @EntityGraph(attributePaths = {"sender"})
    Page<Message> findByChatRoomId(Long chatRoomId, Pageable pageable);
}