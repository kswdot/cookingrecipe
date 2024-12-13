package com.cookingrecipe.cookingrecipe.service;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.dto.BoardWithImageDto;
import com.cookingrecipe.cookingrecipe.repository.RecipeStepRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Component
@RequiredArgsConstructor
public class BoardMapper {

    private final RecipeStepRepository recipeStepRepository;

    // 게시글 + 대표 이미지 반환
    public List<BoardWithImageDto> findBoardsWithMainImages(List<Board> boards) {
        return boards.stream()
                .map(board -> new BoardWithImageDto(board, getMainImage(board.getId())))
                .collect(Collectors.toList());
    }


    // 대표 이미지 설정
    public String getMainImage(Long boardId) {
        String lastImagePath = recipeStepRepository.findLastImagePathByBoardId(boardId);
        return lastImagePath != null ? lastImagePath : "/static/images/default-recipe.jpg";
    }
}
