package com.cookingrecipe.cookingrecipe.service.Board;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.dto.BoardWithImageDto;
import com.cookingrecipe.cookingrecipe.repository.RecipeStepRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Getter
@Component
@RequiredArgsConstructor
@Slf4j
public class BoardMapper {

    private final RecipeStepRepository recipeStepRepository;
    private static final String DEFAULT_IMAGE_PATH = "/static/images/default-recipe.jpg"; // 기본 경로 상수화

    // 게시글 리스트를 BoardWithImageDto 리스트로 변환
    public List<BoardWithImageDto> mapToBoardWithImageDto(List<Board> boards) {
        return boards.stream()
                .map(board -> {
                    String lastImagePath = getMainImage(board.getId());
                    return new BoardWithImageDto(board, lastImagePath);
                })
                .collect(Collectors.toList());
    }

    // 대표 이미지 설정
    public String getMainImage(Long boardId) {
        String lastImagePath = recipeStepRepository.findLastImagePathByBoardId(boardId);
        return lastImagePath != null ? lastImagePath : DEFAULT_IMAGE_PATH;
    }
}
