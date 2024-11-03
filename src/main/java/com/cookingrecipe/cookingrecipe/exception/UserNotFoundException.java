package com.cookingrecipe.cookingrecipe.exception;

import lombok.NoArgsConstructor;

/**
 * 데이터베이스에서 유저 정보를 찾지 못했을 경우 반환되는 오류
 */
@NoArgsConstructor
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
