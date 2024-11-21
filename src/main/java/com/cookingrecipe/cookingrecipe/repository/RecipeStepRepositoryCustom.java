package com.cookingrecipe.cookingrecipe.repository;

public interface RecipeStepRepositoryCustom {

    // 게시글의 마지막 이미지를 대표 이미지로 처리
    String findLastImagePathByBoardId(Long boardId);
}
