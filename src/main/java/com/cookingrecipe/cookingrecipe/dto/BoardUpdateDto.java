package com.cookingrecipe.cookingrecipe.dto;

import com.cookingrecipe.cookingrecipe.domain.Category;
import com.cookingrecipe.cookingrecipe.domain.Method;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BoardUpdateDto {

    @NotBlank(message = "제목을 입력하세요")
    private String title;

    @NotBlank(message = "종류를 선택해주세요")
    private Category category;

    @NotBlank(message = "요리 방법을 선택해주세요")
    private Method method;

    @NotBlank(message = "재료를 입력하세요")
    private String ingredient;

    @NotBlank(message = "내용을 입력하세요")
    private String content;

    @NotNull(message = "이미지를 추가해주세요")
    private List<ImageDto> images;
}
