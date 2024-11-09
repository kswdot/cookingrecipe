package com.cookingrecipe.cookingrecipe.dto;

import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateDto {

    // 읽기 전용
    private String loginId;

    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,20}$",
            message = "닉네임은 특수문자를 제외한 2자 이상이어야 합니다")
    private String nickname;

    @Pattern(regexp = "^(\\w+\\.?)*\\w+@(\\w+\\.)+\\w*$",
            message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @Pattern(regexp = "^010-\\d{4}-\\d{4}$",
            message = "올바른 전화번호를 입력하세요")
    private String number;

    private LocalDate birth;

    public UserUpdateDto(String loginId, String nickname, String email, String number, LocalDate birth) {
        this.loginId = loginId;
        this.nickname = nickname;
        this.email = email;
        this.number = number;
        this.birth = birth;
    }
}
