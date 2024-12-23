package com.cookingrecipe.cookingrecipe.dto.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FindLoginIdDto {

    @NotBlank(message = "번호를 입력하세요")
    private String number;

    @NotNull(message = "생년월일을 입력하세요")
    private LocalDate birth;
}
