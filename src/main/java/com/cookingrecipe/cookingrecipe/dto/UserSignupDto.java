package com.cookingrecipe.cookingrecipe.dto;

import com.cookingrecipe.cookingrecipe.domain.Birth;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserSignupDto {

    @NotNull(message = "로그인 아이디를 입력하세요")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,20}$",
            message = "아이디는 특수 문자를 제외한 5자 이상이어야 합니다")
    private String loginId;


    @NotNull(message = "닉네임을 입력하세요")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,20}$",
            message = "닉네임은 특수문자를 제외한 2자 이상이어야 합니다")
    private String nickname;

    /**
     * (?=.*[0-9]): 숫자(0-9)가 최소 한 개 이상 포함되어야 함
     * (?=.*[a-zA-Z]): 영문자(a-z 또는 A-Z)가 최소 한 개 이상 포함되어야 함
     */
    @NotBlank(message = "비밀번호를 입력하세요")
    @Pattern(regexp = "(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])(?=.*[0-9a-zA-Z]).{8,16}",
            message = "특수문자를 최소 하나 포함하고, 숫자 또는 영문자를 포함한 8~16자여야 합니다")
    private String password;

    @NotBlank(message = "이메일을 입력하세요")
    @Pattern(regexp = "^(\\w+\\.?)*\\w+@(\\w+\\.)+\\w*$",
            message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @NotBlank(message = "전화번호를 입력하세요")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "올바른 전화번호를 입력하세요")
    private String number;

    @NotBlank(message = "생년월일을 입력하세요")
    private Birth birth;

}
