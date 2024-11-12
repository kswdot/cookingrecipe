package com.cookingrecipe.cookingrecipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FindLoginIdDto {

    @NotBlank(message = "번호를 입력하세요")
    private String number;

    @NotNull(message = "생년월일을 입력하세요")
    private LocalDate birth;
}
