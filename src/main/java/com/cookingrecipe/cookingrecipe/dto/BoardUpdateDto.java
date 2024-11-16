package com.cookingrecipe.cookingrecipe.dto;

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

    @NotBlank(message = "재료를 입력하세요")
    private String ingredient;

    @NotBlank(message = "내용을 입력하세요")
    private String content;

    @NotBlank
    private String category;

    @NotNull(message = "이미지를 추가해주세요")
    private List<ImageDto> images;
}
