package com.cookingrecipe.cookingrecipe.domain;

import com.cookingrecipe.cookingrecipe.entity.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@ToString(of = {"id", "nickname", "email"})
public class User extends BaseTimeEntity {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String loginId; // 로그인 시 사용하는 아이디
    private String nickname; // 게시글, 댓글 작성 시 다른 사용자들에게 보여지는 닉네임
    private String password; // 로그인 시 사용하는 비밀번호
    private String email;
    private String number; // 아이디, 비밀번호 찾기 시 사용되는 이메일
    private LocalDate birth; // 아이디, 비밀번호 찾기 시 사용되는 생년월일
    @Enumerated(EnumType.STRING)
    private Role role;


    @Builder
    private User(String loginId, String nickname, String password, String email, String number, LocalDate birth, Role role) {
        this.loginId = loginId;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.number = number;
        this.birth = birth;
        this.role = role;
    }


    // 사용자 정보 수정
    public void updateUser(String nickname, String email, String number) {
        this.nickname = nickname;
        this.email = email;
        this.number = number;
    }


    // 사용자 비밀번호 수정
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }


    // 간편 회원 가입 사용자 정보 추가
    public void addUserInfo(String email, String number, LocalDate birth, String nickname) {
        this.email = email;
        this.number = number;
        this.birth = birth;
        this.nickname = nickname;
    }

    public CustomUserDetails toCustomUserDetails() {
        return new CustomUserDetails(this);
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = LAZY)
    @JsonManagedReference
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = LAZY)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = LAZY)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = LAZY)
    private List<Comment> comments = new ArrayList<>();

}
