package com.cookingrecipe.cookingrecipe.dto.api;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.dto.BoardWithImageDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardResponseDto {

    private Long id;
    private String title;
    private String nickname;
    private String category;
    private String method;
    private String ingredient;
    private String content;
    private String lastImagePath;
    private int likeCount;
    private int bookmarkCount;
    private long view;

    public static BoardResponseDto from(BoardWithImageDto boardWithImageDto) {
        Board board = boardWithImageDto.getBoard();
        return new BoardResponseDto(
                board.getId(),
                board.getTitle(),
                board.getNickname(),
                board.getCategory().name(),
                board.getMethod().name(),
                board.getIngredient(),
                board.getContent(),
                boardWithImageDto.getLastImagePath(),
                board.getLikeCount(),
                board.getBookmarkCount(),
                board.getView()
        );
    }
}
