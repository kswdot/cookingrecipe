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

    private Long id;

    @NotNull(message = "단계 번호를 입력하세요")
    private int stepOrder;

    @NotBlank(message = "단계 설명을 입력하세요")
    private String description;

    private MultipartFile image; // 새로 업로드 하는 이미지
    private String imagePath; // 기존 이미지 경로
}
