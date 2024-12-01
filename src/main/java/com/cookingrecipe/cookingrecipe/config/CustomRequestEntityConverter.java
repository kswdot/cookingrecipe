package com.cookingrecipe.cookingrecipe.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;

public class CustomRequestEntityConverter {

    private final OAuth2AuthorizationCodeGrantRequestEntityConverter delegate =
            new OAuth2AuthorizationCodeGrantRequestEntityConverter();

    public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest request) {
        // 카카오 API에 맞는 요청 파라미터 구성
        MultiValueMap<String, String> formParameters = new LinkedMultiValueMap<>();
        formParameters.add("grant_type", "authorization_code");
        formParameters.add("code", request.getAuthorizationExchange().getAuthorizationResponse().getCode());
        formParameters.add("redirect_uri", "http://localhost:8080/login/oauth2/code/kakao");
        formParameters.add("client_id", request.getClientRegistration().getClientId());
        formParameters.add("client_secret", request.getClientRegistration().getClientSecret());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // RequestEntity로 반환
        return RequestEntity
                .post(URI.create(request.getClientRegistration().getProviderDetails().getTokenUri()))
                .headers(headers)
                .body(formParameters);
    }
}
