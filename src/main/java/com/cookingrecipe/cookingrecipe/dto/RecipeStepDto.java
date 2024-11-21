package com.cookingrecipe.cookingrecipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RecipeStepDto {

    @NotNull(message = "단계 번호를 입력하세요")
    private int stepOrder;

    @NotBlank(message = "설명을 입력하세요")
    private String description;

    private MultipartFile image; // 단계별 이미지
}
