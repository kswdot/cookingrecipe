package com.cookingrecipe.cookingrecipe.repository;

import com.cookingrecipe.cookingrecipe.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b JOIN FETCH b.user WHERE b.id = :boardId")
    Optional<Board> findByIdWithUser(@Param("boardId") Long boardId);

    @Modifying
    @Query("UPDATE Board b SET b.likeCount = b.likeCount + 1 WHERE b.id = :boardId")
    void incrementLikeCount(@Param("boardId") Long boardId);

    @Modifying
    @Query("UPDATE Board b SET b.likeCount = b.likeCount - 1 WHERE b.id = :boardId AND b.likeCount > 0")
    void decrementLikeCount(@Param("boardId") Long boardId);

    @Modifying
    @Query("UPDATE Board b SET b.view = b.view + 1 WHERE b.id = :boardId")
    void updateViewCount(@Param("boardId") Long boardId);

    // 특정 게시글과 해당 게시글에 속한 레시피 조회
    @Query("SELECT b FROM Board b LEFT JOIN FETCH b.recipeSteps WHERE b.id = :boardId")
    Optional<Board> findWithRecipeStepsById(@Param("boardId") Long boardId);
}
