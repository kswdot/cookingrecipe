package com.cookingrecipe.cookingrecipe.repository;

import com.cookingrecipe.cookingrecipe.domain.Like;
import com.cookingrecipe.cookingrecipe.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // 좋아요 여부 조회
    Optional<Like> findByBoardIdAndUserId(Long boardId, Long userId);
}
