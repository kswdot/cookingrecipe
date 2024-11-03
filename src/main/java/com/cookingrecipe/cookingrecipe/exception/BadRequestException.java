package com.cookingrecipe.cookingrecipe.exception;

/**
 * 정보가 잘못 입력되었을 경우 반환되는 오류
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
