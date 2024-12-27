package com.cookingrecipe.cookingrecipe.domain;

import com.cookingrecipe.cookingrecipe.domain.User.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Message {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false) // 보낸 사람
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false) // 받은 사람
    private User receiver;

    @Column(nullable = false)
    private String content;   // 메시지 내용

    @CreationTimestamp // 현재 시간 자동 삽입
    private LocalDateTime sendTime;

    private boolean readStatus = false; // 읽음 여부 기본값
}
