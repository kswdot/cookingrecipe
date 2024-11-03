package com.cookingrecipe.cookingrecipe.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // 개발 단계에서는 CSRF 보호 비활성화
                .authorizeRequests(auth -> auth
                        .requestMatchers("/", "/login", "/join").permitAll() // 모두 접근 가능
                        .requestMatchers("/member/**").authenticated() // 인증된 사용자만 접근 가능
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자만 접근 가능
                )
                .formLogin(form -> form // 로그인 설정
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }
}
