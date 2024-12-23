package com.cookingrecipe.cookingrecipe.dto.Board;

import com.cookingrecipe.cookingrecipe.domain.Board.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
// 대표 사진을 레시피의 마지막 사진으로 설정하기 위해 받는 DTO
public class BoardWithImageDto {

    private Board board;
    private String lastImagePath;
}
