package com.cookingrecipe.cookingrecipe.config;

import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oauth2User;
    @Setter
    private String customNickname;

    public CustomOAuth2User(OAuth2User oauth2User) {
        this.oauth2User = oauth2User;
        this.customNickname = (String) oauth2User.getAttributes().get("nickname");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oauth2User.getName();
    }

    public String getNickname() {
        return customNickname != null
                ? customNickname
                : (String) oauth2User.getAttributes().get("nickname");
    }

    // 추가 정보 설정
    public String getCustomNickname() {
        return customNickname != null
                ? customNickname
                : oauth2User.getAttributes().getOrDefault("nickname", "사용자").toString();

    }

}
