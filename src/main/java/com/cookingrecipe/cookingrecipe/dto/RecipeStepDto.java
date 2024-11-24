package com.cookingrecipe.cookingrecipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStepDto {

    @NotNull(message = "단계 번호를 입력하세요")
    private int stepOrder;

    @NotBlank(message = "단계 설명을 입력하세요")
    private String description;

    private MultipartFile image; // 단계별 이미지
}
