package com.cookingrecipe.cookingrecipe.repository;

import com.cookingrecipe.cookingrecipe.domain.RecipeStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long>, RecipeStepRepositoryCustom {

    void deleteByBoardId(Long boardId);

    List<RecipeStep> findByBoardId(Long boardId);
}
