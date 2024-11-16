package com.cookingrecipe.cookingrecipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class BoardSaveDto {

    @NotBlank(message = "제목을 입력하세요")
    private String title;

    @NotBlank(message = "재료를 입력하세요")
    private String ingredient;

    @NotBlank(message = "내용을 입력하세요")
    private String content;

    @NotBlank
    private String category;

    @NotBlank
    private String nickname; // 게시글 표시

    @NotNull
    private Long userId; // 사용자 식별

    @NotNull(message = "이미지를 추가해주세요")
    private List<MultipartFile> images;
}
