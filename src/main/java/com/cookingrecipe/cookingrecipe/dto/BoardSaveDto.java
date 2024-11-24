package com.cookingrecipe.cookingrecipe.dto;

import com.cookingrecipe.cookingrecipe.domain.Category;
import com.cookingrecipe.cookingrecipe.domain.Method;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardSaveDto {

    @NotBlank(message = "제목을 입력하세요")
    private String title;

    @NotNull(message = "종류를 선택하세요")
    private Category category;

    @NotNull(message = "요리 방법을 선택하세요")
    private Method method;

    @NotBlank(message = "재료를 입력하세요")
    private String ingredient;

    @NotBlank(message = "간단한 요리 설명을 입력하세요")
    private String content;

    @NotNull(message = "최소한 한 단계 이상의 레시피를 입력하세요")
    private List<RecipeStepDto> recipeSteps = new ArrayList<>();

    @NotBlank
    private String nickname; // 게시글 표시

    @NotNull
    private Long userId; // 사용자 식별

}
