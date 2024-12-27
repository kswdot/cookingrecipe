package com.cookingrecipe.cookingrecipe.repository;

import com.cookingrecipe.cookingrecipe.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // 읽지 않은 메시지 개수 조회
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :receiverId AND m.readStatus = false")
    int countUnreadMessages(@Param("receiverId") Long receiverId);

    // 특정 사용자가 보낸 메시지 조회
    @Query("SELECT m FROM Message m WHERE m.sender.id = :senderId ORDER BY m.sendTime DESC")
    List<Message> findSentMessages(@Param("senderId") Long senderId);

    // 특정 사용자가 받은 메시지 조회
    @Query("SELECT m FROM Message m WHERE m.receiver.id = :receiverId ORDER BY m.sendTime DESC")
    List<Message> findReceivedMessages(@Param("receiverId") Long receiverId);
}
