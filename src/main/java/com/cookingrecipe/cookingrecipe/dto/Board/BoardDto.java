package com.cookingrecipe.cookingrecipe.dto.Board;

import com.cookingrecipe.cookingrecipe.domain.Board.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
// 대표 사진을 레시피의 마지막 사진으로 설정하기 위해 받는 DTO
public class BoardDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String nickname;
    private String category;
    private String method;
    private String ingredient;
    private String content;
    private String lastImagePath; // 대표 이미지 경로
    private int likeCount;
    private int bookmarkCount;
    private long view;

    public static BoardDto from(Board board, String lastImagePath) {
        return new BoardDto(
                board.getId(),
                board.getTitle(),
                board.getNickname(),
                board.getCategory().name(),
                board.getMethod().name(),
                board.getIngredient(),
                board.getContent(),
                lastImagePath, // 대표 이미지 경로를 파라미터로 전달받음
                board.getLikeCount(),
                board.getBookmarkCount(),
                board.getView()
        );
    }
}
