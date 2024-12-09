package com.cookingrecipe.cookingrecipe.repository;

import com.cookingrecipe.cookingrecipe.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 모든 알림 조회
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdAt DESC")
    List<Notification> findAllByUserId(@Param("userId") Long userId);

    // 읽지 않은 알림 조회
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.isRead = FALSE ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);

    // 모든 알림 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = TRUE WHERE n.userId =:userId")
    void markAllAsRead(@Param("userId") Long userId);

    // 특정 알림 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = TRUE WHERE n.userId =:userId AND n.id =:notificationId")
    void markAsRead(@Param("userId") Long userId, @Param("notificationId") Long notificationId);

    // 모든 알림 삭제
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.userId =:userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    // 특정 알림 삭제
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.userId = :userId AND n.id =:notificationId")
    void deleteByUserId(@Param("userId") Long userId, @Param("notificationId") Long notificationId);
}
