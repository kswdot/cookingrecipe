package com.cookingrecipe.cookingrecipe.repository.RecipeStep;

import com.cookingrecipe.cookingrecipe.domain.QRecipeStep;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RecipeStepRepositoryCustomImpl implements RecipeStepRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    // 게시글의 마지막 이미지를 대표 이미지로 처리
    @Override
    public String findLastImagePathByBoardId(Long boardId) {
        QRecipeStep recipeStep = QRecipeStep.recipeStep;

        // 마지막 이미지 경로를 쿼리로 가져옵니다.
        List<String> imagePaths = queryFactory
                .select(recipeStep.imagePath)
                .from(recipeStep)
                .where(recipeStep.board.id.eq(boardId))
                .orderBy(recipeStep.stepOrder.desc()) // stepOrder 내림차순 정렬
                .fetch(); // 모든 이미지를 반환

        if (imagePaths != null && !imagePaths.isEmpty()) {
            // 마지막 이미지 반환 (정렬되어 있으므로 첫 번째가 가장 마지막 이미지)
            return imagePaths.get(0);
        } else {
            log.info("No image found for boardId: {}", boardId);  // 이미지가 없을 경우
            return null;
        }
    }

}
