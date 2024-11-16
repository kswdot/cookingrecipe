package com.cookingrecipe.cookingrecipe.repository;

import com.cookingrecipe.cookingrecipe.domain.Board;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepositoryCustom {

    // 검색 조건에 따라서 게시글 조회
    List<Board> searchBoards(String title, String material, String writer);

    // 카테고리에 따른 게시글 조회
    List<Board> findByCategory(String category);

    // 사용자의 ID로 게시글 조회
    List<Board> findByUserId(Long userId);

    // 사용자의 북마크에 저장된 게시글 조회
    List<Board> findBookmarkedRecipeByUser(Long userId);

    // 인기 레시피 TOP10
    List<Board> findTopRecipesByLikes(int limit);

    // 이 달(한 달)의 레시피 TOP10
    List<Board> findMonthlyRecipesByLikes(int limit);


}
