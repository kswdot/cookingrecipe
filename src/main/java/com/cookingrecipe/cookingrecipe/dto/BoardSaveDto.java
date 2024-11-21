package com.cookingrecipe.cookingrecipe.dto;

import com.cookingrecipe.cookingrecipe.domain.Category;
import com.cookingrecipe.cookingrecipe.domain.Method;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
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

    @NotBlank
    private String nickname; // 게시글 표시

    @NotNull
    private Long userId; // 사용자 식별

}
