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
                ) // 로그인 설정
                .formLogin(form -> form
                        .loginPage("/login") // 로그인 페이지 경로
                        .defaultSuccessUrl("/") // 로그인 성공 시 이동할 경로
                        .loginProcessingUrl("/login") // 로그인 폼에서 제출할 경로 (POST)
                        .usernameParameter("loginId") // 로그인 폼 아이디
                        .passwordParameter("password") // 로그인 폼 비밀번호
                        .failureUrl("/login?error=true") // 로그인 실패 시 리다이렉트 경로
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // 쿠키 삭제
                );

        return http.build();
    }
}
