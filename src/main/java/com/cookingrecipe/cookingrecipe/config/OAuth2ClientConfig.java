package com.cookingrecipe.cookingrecipe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;

public class OAuth2ClientConfig {

    @Bean
    public DefaultAuthorizationCodeTokenResponseClient customAuthorizationCodeTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient client = new DefaultAuthorizationCodeTokenResponseClient();
        // 1단계에서 만든 변환기를 설정
        client.setRequestEntityConverter(new CustomRequestEntityConverter()::convert);
        return client;
    }
}
