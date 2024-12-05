package com.cookingrecipe.cookingrecipe.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentRequestDto {

    @NotBlank
    private String content;

    private Long boardId;

    private Long userId;
}
