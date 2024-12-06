package com.cookingrecipe.cookingrecipe.dto;

import com.cookingrecipe.cookingrecipe.domain.Category;
import com.cookingrecipe.cookingrecipe.domain.Method;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardUpdateDto {

    @NotBlank(message = "제목을 입력하세요")
    private String title;

    @NotNull(message = "종류를 선택해주세요")
    private Category category;

    @NotNull(message = "요리 방법을 선택해주세요")
    private Method method;

    @NotBlank(message = "재료를 입력하세요")
    private String ingredient;

    @NotBlank(message = "내용을 입력하세요")
    private String content;

    @NotNull(message = "최소한 한 단계 이상의 레시피를 입력하세요")
    private List<RecipeStepDto> recipeSteps = new ArrayList<>();
}