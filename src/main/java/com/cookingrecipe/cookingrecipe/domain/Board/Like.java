package com.cookingrecipe.cookingrecipe.domain.Board;

import com.cookingrecipe.cookingrecipe.domain.BaseTimeEntity;
import com.cookingrecipe.cookingrecipe.domain.User.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static jakarta.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@ToString(of = {"id","status"})
@Table(name = "likes")
public class Like extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @NotNull
    private boolean status;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public Like(User user, Board board) {
        this.user = user;
        this.board = board;
    }
}
