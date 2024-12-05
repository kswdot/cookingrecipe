package com.cookingrecipe.cookingrecipe.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentResponseDto {

    private Long id;

    private Long userId;

    @NotBlank
    private String content;

    private String nickname;

    private LocalDateTime createdAt;
}
