package com.cookingrecipe.cookingrecipe.service;

import com.cookingrecipe.cookingrecipe.domain.Notification;

import java.util.List;

public interface NotificationService {


    // 모든 알림 조회
    List<Notification> getAllNotifications(Long userId);

    // 읽지 않은 알림 조회
    List<Notification> getUnreadNotifications(Long userId);

    // 모든 알림 읽음 처리
    void markAllAsRead(Long userId);

    // 특정 알림 읽음 처리
    void markAsRead(Long userId, Long notificationId);

    // 모든 알림 삭제
    void deleteAllNotifications(Long userId);

    // 특정 알림 삭제
    void deleteNotification(Long userId, Long notificationId);


}
