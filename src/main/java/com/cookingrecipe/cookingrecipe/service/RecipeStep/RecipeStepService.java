package com.cookingrecipe.cookingrecipe.service.RecipeStep;

import com.cookingrecipe.cookingrecipe.domain.Board.Board;
import com.cookingrecipe.cookingrecipe.domain.Board.RecipeStep;
import com.cookingrecipe.cookingrecipe.dto.RecipeStepDto;

import java.io.IOException;
import java.util.List;

public interface RecipeStepService {

    List<RecipeStep> findByBoardId(Long boardId);

    void saveRecipeSteps(Board board, List<RecipeStepDto> recipeStepDto) throws IOException;

    void updateRecipeSteps(Board board, List<RecipeStep> existingSteps, List<RecipeStepDto> stepDtos, String uploadDir) throws IOException;

    void saveNewStep(Board board, RecipeStepDto stepDto, String uploadDir) throws IOException;

    void updateExistingStep(RecipeStep existingStep, RecipeStepDto stepDto, String uploadDir) throws IOException;

    void deleteExistingStep(RecipeStep step, String uploadDir) throws IOException;
}
