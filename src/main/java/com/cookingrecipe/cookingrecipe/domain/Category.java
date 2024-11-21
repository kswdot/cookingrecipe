package com.cookingrecipe.cookingrecipe.domain;


public enum Category {

    KOREAN("한식"), // 한식
    CHINESE("중식"), // 중식
    JAPANESE("일식"), // 일식
    WESTERN("양식"), // 양식
    SOUTHEAST("동남아식"), // 동남아식
    FUSION("퓨전"), // 퓨전
    DESSERT("후식"), // 후식
    ETC("기타"); // 기타

    private final String koreanName;

    Category(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
    
}
