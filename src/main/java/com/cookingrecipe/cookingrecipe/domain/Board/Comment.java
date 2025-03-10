package com.cookingrecipe.cookingrecipe.domain.Board;

import com.cookingrecipe.cookingrecipe.domain.BaseTimeEntity;
import com.cookingrecipe.cookingrecipe.domain.User.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    @JsonBackReference
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
