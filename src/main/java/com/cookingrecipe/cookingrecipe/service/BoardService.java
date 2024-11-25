package com.cookingrecipe.cookingrecipe.service;

import com.cookingrecipe.cookingrecipe.domain.*;
import com.cookingrecipe.cookingrecipe.dto.BoardSaveDto;
import com.cookingrecipe.cookingrecipe.dto.BoardUpdateDto;
import com.cookingrecipe.cookingrecipe.dto.BoardWithImageDto;
import com.cookingrecipe.cookingrecipe.dto.RecipeStepDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public interface BoardService {

    public Board joinEntity(Board board);

    // 게시글 작성
    Long save(BoardSaveDto boardSaveDto, List<RecipeStepDto> recipeStepDto, CustomUserDetails userDetails) throws IOException;

    // 게시글 수정
    Long update(Long boardId, BoardUpdateDto boardUpdateDto, List<RecipeStepDto> recipeStepDto) throws IOException;

    // 모든 게시글 검색
    List<BoardWithImageDto> findAll();

    // 모든 게시글 검색 - 최신순
    List<BoardWithImageDto> findAllByDateDesc();

    // 게시글 검색 - 유저
    Optional<Board> findByUser(Long boardId);

    // 게시글 검색 - 시스템 ID
    Board findById(Long boardId);

    // 특정 게시글과 속한 레시피 검색
    Board findBoardWithRecipeSteps(Long boardId);

    // 게시글 검색 - 검색 조건 - 최신순
    List<BoardWithImageDto> searchBoards(String searchCriteria, String keyword);

    // 게시글 검색 -검색 조건 - 좋아요 순
    List<BoardWithImageDto> searchBoardsOrderByLikes(String searchCriteria, String keyword);

    // 게시글 검색 - 카테고리 - 최신순
    List<BoardWithImageDto> findByCategory(Category category);

    // 게시글 검색 - 카테고리 - 좋아요 순
    List<BoardWithImageDto> findByCategoryOrderByLikes(Category category);

    // 게시글 검색 - 요리 방법 - 최신순
    List<BoardWithImageDto> findByMethod(Method method);

    // 게시글 검색 - 요리 방법 - 좋아요 순
    List<BoardWithImageDto> findByMethodOrderByLikes(Method method);

    // 게시글 검색 - 인기 레시피 TOP 10
    List<BoardWithImageDto> findTopRecipesByLikes(int limit);

    // 게시글 검색 - 이 달(한 달)의 레시피 TOP10
    List<BoardWithImageDto> findMonthlyRecipesByLikes(int limit);

    // 조회수 증가
    void addViewCount(Long boardId);

    // 게시글 삭제
    void deleteById(Long boardId);

    // 좋아요 추가
    void addLike(Long boardId);

    // 좋아요 삭제
    void removeLike(Long boardId);

    // 좋아요 여부 확인
    boolean isLikedByUser(Long boardId, Long userId);

    // 책갈피 여부 확인
    boolean isBookmarkedByUser(Long boardId, Long userId);

    // 좋아요 추가/삭제 토글
    void toggleLike(Long boardId, Long userId);

    // 북마크 추가/삭제 토글
    void toggleBookmark(Long boardId, Long userId);

    // InitData 삽입 위한 메서드 생성
    void saveForInitData(BoardSaveDto boardSaveDto, List<RecipeStepDto> recipeStepDto, User user);


}