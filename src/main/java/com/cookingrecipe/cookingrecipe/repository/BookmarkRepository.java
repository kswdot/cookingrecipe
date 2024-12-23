package com.cookingrecipe.cookingrecipe.repository;

import com.cookingrecipe.cookingrecipe.domain.Board.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // 북마크 여부 조회
    Optional<Bookmark> findByBoardIdAndUserId(Long boardId, Long userId);

}
