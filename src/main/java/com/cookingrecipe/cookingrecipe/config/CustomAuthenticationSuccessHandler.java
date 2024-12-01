package com.cookingrecipe.cookingrecipe.config;

import com.cookingrecipe.cookingrecipe.domain.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final HttpSession httpSession;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        Boolean additionalInfoRequired = (Boolean) httpSession.getAttribute("additional_info_required");

        if (Boolean.TRUE.equals(additionalInfoRequired)) {
            // 추가 정보 입력 페이지로 리다이렉트
            response.sendRedirect("/additional-info");
        } else {
            // 메인 페이지로 리다이렉트
            response.sendRedirect("/");
        }
    }
}
