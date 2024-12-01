package com.cookingrecipe.cookingrecipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialSignupDto {

    @NotBlank(message = "사용할 닉네임을 입력하세요")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,20}$",
            message = "닉네임은 특수문자를 제외한 2자 이상이어야 합니다")
    private String nickname;

    @NotBlank(message = "이메일을 입력하세요")
    @Pattern(regexp = "^(\\w+\\.?)*\\w+@(\\w+\\.)+\\w*$",
            message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @NotBlank(message = "전화번호를 입력하세요")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "올바른 전화번호를 입력하세요")
    private String number;

    @NotNull(message = "생년월일을 입력하세요")
    private LocalDate birth;
}
