package com.cookingrecipe.cookingrecipe.domain.Board;

import com.cookingrecipe.cookingrecipe.domain.BaseTimeEntity;
import com.cookingrecipe.cookingrecipe.domain.User.User;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@ToString(of = {"id"})
public class Bookmark extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public Bookmark(User user, Board board) {
        this.user = user;
        this.board = board;
    }
}
