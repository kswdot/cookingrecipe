package com.cookingrecipe.cookingrecipe.service;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.domain.Category;
import com.cookingrecipe.cookingrecipe.domain.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.domain.Method;
import com.cookingrecipe.cookingrecipe.dto.BoardSaveDto;
import com.cookingrecipe.cookingrecipe.dto.BoardUpdateDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public interface BoardService {

    // 게시글 작성
    Long save(BoardSaveDto boardSaveDto, List<MultipartFile> images, CustomUserDetails userDetails);

    // 게시글 수정
    Long update(Long boardId, BoardUpdateDto boardUpdateDto);

    // 모든 게시글 검색
    List<Board> findAll();

    // 모든 게시글 검색 - 최신순
    List<Board> findAllByDateDesc();

    // 게시글 검색 - 유저
    Optional<Board> findByUser(Long boardId);

    // 게시글 검색 - 시스템 ID
    Board findById(Long boardId);

    // 게시글 검색 - 검색 조건 - 최신순
    List<Board> searchBoards(String keyword, String material, String writer);

    // 게시글 검색 -검색 조건 - 좋아요 순
    List<Board> searchBoardsOrderByLikes(String keyword, String material, String writer);

    // 게시글 검색 - 카테고리 - 최신순
    List<Board> findByCategory(Category category);

    // 게시글 검색 - 카테고리 - 좋아요 순
    List<Board> findByCategoryOrderByLikes(Category category);

    // 게시글 검색 - 요리 방법 - 최신순
    List<Board> findByMethod(Method method);

    // 게시글 검색 - 요리 방법 - 좋아요 순
    List<Board> findByMethodOrderByLikes(Method method);

    // 게시글 검색 - 인기 레시피 TOP 10
    List<Board> findTopRecipesByLikes(int limit);

    // 게시글 검색 - 이 달(한 달)의 레시피 TOP10
    List<Board> findMonthlyRecipesByLikes(int limit);

    // 조회수 증가
    void addViewCount(Long boardId);

    // 게시글 삭제
    void deleteById(Long boardId);

    // 좋아요 추가
    void addLike(Long boardId);
    
    // 좋아요 삭제
    void removeLike(Long boardId);
    
    // 북마크 추가/삭제 토글
    boolean toggleBookmark(Long boardId, Long userId);



}
