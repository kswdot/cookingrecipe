package com.cookingrecipe.cookingrecipe.service.Board;

import com.cookingrecipe.cookingrecipe.domain.Board.Board;
import com.cookingrecipe.cookingrecipe.domain.Board.Category;
import com.cookingrecipe.cookingrecipe.domain.Board.Method;
import com.cookingrecipe.cookingrecipe.domain.User.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.domain.User.User;
import com.cookingrecipe.cookingrecipe.dto.*;
import com.cookingrecipe.cookingrecipe.dto.Board.BoardDto;
import com.cookingrecipe.cookingrecipe.dto.Board.BoardSaveDto;
import com.cookingrecipe.cookingrecipe.dto.Board.BoardUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public interface BoardService {

    // 게시글 작성
    Long save(BoardSaveDto boardSaveDto, List<RecipeStepDto> recipeStepDto, CustomUserDetails userDetails) throws IOException;

    // 게시글 수정
    Long update(Long boardId, BoardUpdateDto boardUpdateDto, List<RecipeStepDto> recipeStepDto) throws IOException;

    // 모든 게시글 검색
    List<BoardDto> findAll();

    // 모든 게시글 검색 - 최신순
    Page<BoardDto> findAllByDateDesc(Pageable pageable);

    // 게시글 검색 - 유저
    Optional<Board> findByIdWithUser(Long userId);

    // 게시글 검색 - 시스템 ID
    Board findById(Long boardId);

    // 특정 게시글과 속한 레시피 검색
    Board findBoardWithRecipeSteps(Long boardId);

    // 게시글 검색 - 검색 조건 - 최신순
    Page<BoardDto> searchBoards(String searchCriteria, String keyword, Pageable pageable);

    // 게시글 검색 -검색 조건 - 좋아요 순
    Page<BoardDto> searchBoardsOrderByLikes(String searchCriteria, String keyword, Pageable pageable);

    // 게시글 검색 - 카테고리 - 최신순
    List<BoardDto> findByCategory(Category category);

    // 게시글 검색 - 카테고리 - 좋아요 순
    List<BoardDto> findByCategoryOrderByLikes(Category category);

    // 게시글 검색 - 요리 방법 - 최신순
    List<BoardDto> findByMethod(Method method);

    // 게시글 검색 - 요리 방법 - 좋아요 순
    List<BoardDto> findByMethodOrderByLikes(Method method);

    // 게시글 검색 - 인기 레시피 TOP 10
    List<BoardDto> findTopRecipesByLikes(int limit);

    // 게시글 검색 - 이 달(한 달)의 레시피 TOP10
    List<BoardDto> findMonthlyRecipesByLikes(int limit);

    // 조회수 증가 - HTTP Session 사용
    void addViewCount(Long boardId);

    // 조회수 증가 - Redis 사용
    boolean addViewCountWithRedis(Long boardId, Long userId);

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
    void saveForInitData(InitBoardSaveDto initBoardSaveDto, List<RecipeStepDto> recipeStepDto, User user, LocalDateTime createdDate);


}