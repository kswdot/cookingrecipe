package com.cookingrecipe.cookingrecipe.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public enum Category {

    KOREAN, // 한식
    CHINESE, // 중식
    JAPANESE, // 일식
    WESTERN, // 양식
    SOUTHEAST, // 동남아식
    FUSION, // 퓨전
    DESSERT, // 후식
    ETC // 기타
}
