package com.cookingrecipe.cookingrecipe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
public class WebSecurityCustomizerConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(
                        "/swagger-ui/**", "/v3/api-docs/**", "/images/**", "/css/**", "/js/**", "/static/**", "/uploads/**"
                );
    }
}
