package com.cookingrecipe.cookingrecipe.dto;

import com.cookingrecipe.cookingrecipe.domain.Board.Category;
import com.cookingrecipe.cookingrecipe.domain.Board.Method;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InitBoardSaveDto {
    private String title;
    private String content;
    private Category category;
    private Method method;
    private String ingredient;
    private String nickname;
    private Long userId;
    private int likeCount;
    private LocalDateTime createdDate;
}
