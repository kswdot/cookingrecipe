package com.cookingrecipe.cookingrecipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PasswordUpdateDto {

    @NotBlank(message = "현재 비밀번호를 입력하세요")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호를 입력하세요")
    @Pattern(regexp = "(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])(?=.*[0-9a-zA-Z]).{8,16}",
            message = "특수문자를 최소 하나 포함하고, 숫자 또는 영문자를 포함한 8~16자여야 합니다")
    private String newPassword;

    @NotBlank(message = "비밀번호 확인을 입력하세요")
    @Pattern(regexp = "(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])(?=.*[0-9a-zA-Z]).{8,16}",
            message = "특수문자를 최소 하나 포함하고, 숫자 또는 영문자를 포함한 8~16자여야 합니다")
    private String confirmPassword;
}
