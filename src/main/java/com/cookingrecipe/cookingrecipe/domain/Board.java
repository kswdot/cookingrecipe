package com.cookingrecipe.cookingrecipe.domain;

import com.cookingrecipe.cookingrecipe.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
public class Board extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @NotNull
    private String title;
    @NotNull
    private String nickname;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Method method;
    @NotNull
    private String ingredient;
    @NotNull
    private String content;
    @NotNull
    private long view;

    @NotNull
    private int bookmarkCount;
    @NotNull
    private int likeCount;


    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = LAZY)
    @Builder.Default
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = LAZY)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = LAZY)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = LAZY)
    @Builder.Default
    private List<Image> images = new ArrayList<>();


    @Builder
    public Board(String title, Category category, Method method, String ingredient, String content, User user, String nickname) {
        this.title = title;
        this.category = category;
        this.method = method;
        this.ingredient = ingredient;
        this.content = content;
        this.user = user;
        this.nickname = nickname;
        this.view = 0L;
        this.bookmarkCount = 0;
        this.likeCount = 0;
    }


    public void update(String title, Category category, Method method, String ingredient, String content) {
        this.title = title;
        this.category = category;
        this.method = method;
        this.ingredient = ingredient;
        this.content = content;
    }

    /**
     * 메서드 구현
     */

    // 좋아요 수 증가 메서드
    public void incrementLikeCount() {
        this.likeCount++;
    }

    // 좋아요 수 감소 메서드
    public void decrementLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1); // 0 이하로 내려가지 않도록 설정
    }

    // 조회수 증가 메서드
    public void incrementView() {
        this.view++;
    }

    // 북마크 수 증가 메서드
    public void incrementBookmarkCount() {
        this.bookmarkCount++;
    }

    // 북마크 수 감소 메서드
    public void decrementBookmarkCount() {
        this.bookmarkCount = Math.max(0, this.bookmarkCount - 1);
    }

}
