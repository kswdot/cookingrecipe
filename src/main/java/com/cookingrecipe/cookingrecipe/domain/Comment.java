package com.cookingrecipe.cookingrecipe.domain;

import com.cookingrecipe.cookingrecipe.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static jakarta.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@ToString(of = {"id","comment"})
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @NotNull
    private String comment;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "comment_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "comment_id")
    private Board board;

}
