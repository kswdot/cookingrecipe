package com.cookingrecipe.cookingrecipe.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public enum Method {

    STIR_FRY,        // 볶음
    STEAM,      // 찜
    SOUP,       // 탕
    DEEP_FRY,   // 튀김
    GRILL,      // 구이
    MIX,        // 무침
    RAW,        // 회
    SALAD,      // 샐러드
    ETC         // 기타
}
