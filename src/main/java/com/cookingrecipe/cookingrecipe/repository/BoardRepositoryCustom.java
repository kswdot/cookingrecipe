package com.cookingrecipe.cookingrecipe.repository;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.domain.Category;
import com.cookingrecipe.cookingrecipe.domain.Method;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepositoryCustom {

    // 모든 게시글 검색 - 최신순
    Page<Board> findAllByDateDesc(Pageable pageable);

    // 게시글 조회 - 검색조건 - 최신순
    Page<Board> searchBoards(String searchCriteria, String keyword, Pageable pageable);

    // 게시글 조회 - 검색조건 - 좋아요 순
    Page<Board> searchBoardsOrderByLikes(String searchCriteria, String keyword, Pageable pageable);

    // 게시글 조회 - 카테고리 - 최신순
    List<Board> findByCategory(Category category);

    // 게시글 조회 - 카테고리 - 좋아요순
    List<Board> findByCategoryOrderByLikes(Category category);

    // 게시글 조회 - 요리 방법 - 최신순
    List<Board> findByMethod(Method method);

    // 게시글 조회 - 요리 방법 - 좋아요순
    List<Board> findByMethodOrderByLikes(Method method);

    // 마이페이지 - 내가 쓴 글 조회
    List<Board> findByUserId(Long userId);

    // 마이페이지 - 북마크한 글 조회
    List<Board> findBookmarkedRecipeByUser(Long userId);

    // 인기 레시피 TOP10
    List<Board> findTopRecipesByLikes(int limit);

    // 이 달(한 달)의 레시피 TOP10
    List<Board> findMonthlyRecipesByLikes(int limit);

    // 게시글 조회 수 증가 - Redis 사용
    public boolean addViewCountWithRedis(Long boardId, Long userId);

}
