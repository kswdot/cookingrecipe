package com.cookingrecipe.cookingrecipe.domain;

import com.cookingrecipe.cookingrecipe.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@ToString(of = {"id", "username", "password", "nickname", "email"})
public class User extends BaseTimeEntity {


    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String nickname;
    @NotNull
    private String email;


    @OneToMany(mappedBy = "user", fetch = LAZY)
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = LAZY)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = LAZY)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = LAZY)
    private List<Comment> comments = new ArrayList<>();

}
