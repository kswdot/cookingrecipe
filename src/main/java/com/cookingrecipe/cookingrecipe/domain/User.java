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
@ToString(of = {"id", "nickname",  "password", "email"})
public class User extends BaseTimeEntity {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotNull
    private String loginId; // 로그인 시 사용하는 아이디
    @NotNull
    private String nickname; // 게시글, 댓글 작성 시 다른 사용자들에게 보여지는 닉네임
    @NotNull
    private String password; // 로그인 시 사용하는 비밀번호
    @NotNull
    private String email;
    @NotNull
    private String number; // 아이디, 비밀번호 찾기 시 사용되는 이메일
    @NotNull
    @Embedded
    private Birth birth; // 아이디, 비밀번호 찾기 시 사용되는 생년월일

    @Builder
    private User(String loginId, String nickname, String password, String email, String number, Birth birth) {
        this.loginId = loginId;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.number = number;
        this.birth = birth;
    }

    public void updateUser(String nickname, String email, String number) {
        this.nickname = nickname;
        this.email = email;
        this.number = number;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    @OneToMany(mappedBy = "user", fetch = LAZY)
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = LAZY)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = LAZY)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = LAZY)
    private List<Comment> comments = new ArrayList<>();

}
