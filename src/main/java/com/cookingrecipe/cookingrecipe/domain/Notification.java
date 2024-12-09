package com.cookingrecipe.cookingrecipe.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private Long userId; // 알림을 받는 사용자의 ID

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private Long boardId; // 게시글 ID (필요 시 사용)
    private Long commentId; // 댓글 ID (필요 시 사용)
    private String commentContent; // 댓글 내용 (옵션)

    private boolean isRead; // 읽음 여부

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
