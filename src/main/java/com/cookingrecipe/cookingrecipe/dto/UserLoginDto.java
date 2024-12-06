package com.cookingrecipe.cookingrecipe.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserLoginDto {

    @NotBlank(message = "아이디를 입력하세요")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력하세요")
    private String password;
}
