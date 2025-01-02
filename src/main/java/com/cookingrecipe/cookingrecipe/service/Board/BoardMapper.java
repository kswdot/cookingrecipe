package com.cookingrecipe.cookingrecipe.service.Board;

import com.cookingrecipe.cookingrecipe.domain.Board.Board;
import com.cookingrecipe.cookingrecipe.dto.Board.BoardDto;
import com.cookingrecipe.cookingrecipe.repository.RecipeStep.RecipeStepRepository;
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
    public List<BoardDto> mapToBoardDto(List<Board> boards) {
        return boards.stream()
                .map(board -> new BoardDto(
                        board.getId(),
                        board.getTitle(),
                        board.getNickname(),
                        board.getCategory().name(),
                        board.getMethod().name(),
                        board.getIngredient(),
                        board.getContent(),
                        getMainImage(board.getId()), // 대표 이미지 경로
                        board.getLikeCount(),
                        board.getBookmarkCount(),
                        board.getView()
                ))
                .collect(Collectors.toList());
    }

    // 대표 이미지 설정
    public String getMainImage(Long boardId) {
        String lastImagePath = recipeStepRepository.findLastImagePathByBoardId(boardId);
        return lastImagePath != null ? lastImagePath : DEFAULT_IMAGE_PATH;
    }
}
