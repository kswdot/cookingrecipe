package com.cookingrecipe.cookingrecipe.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static jakarta.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @NotNull
    @Column(nullable = false, length = 500)
    private String content; // 내용


    @Builder
    public Comment(User user, Board board, String content) {
        this.user = user;
        this.board = board;
        this.content = content;
    }


    /**
     * 편의 메서드
     */

    // 댓글 내용 수정
    public void updateContent(String content) {
        this.content = content;
    }

}
