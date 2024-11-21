package com.cookingrecipe.cookingrecipe.domain;

public enum Method {

    STIR_FRY("볶음"),        // 볶음
    STEAM("찜"),      // 찜
    SOUP("탕"),       // 탕
    DEEP_FRY("튀김"),   // 튀김
    GRILL("구이"),      // 구이
    MIX("무침"),        // 무침
    RAW("회"),        // 회
    SALAD("샐러드"),      // 샐러드
    ETC("기타");// 기타

    private final String koreanName;

    Method(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
