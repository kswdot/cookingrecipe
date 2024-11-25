package com.cookingrecipe.cookingrecipe.dto;

import com.cookingrecipe.cookingrecipe.domain.Category;
import com.cookingrecipe.cookingrecipe.domain.Method;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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
