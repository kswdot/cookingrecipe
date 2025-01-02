package com.cookingrecipe.cookingrecipe.config;

import com.cookingrecipe.cookingrecipe.service.User.UserDetailsServiceImpl;
import com.cookingrecipe.cookingrecipe.util.JwtAuthenticationFilter;
import com.cookingrecipe.cookingrecipe.util.JwtTokenProvider;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final HttpSession httpSession;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    // 회원가입 -> 자동 로그인
    // AuthenticationManager 직접 등록하지 않고 AuthenticationConfiguration 통해 제공
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean // 비밀번호 암호화
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler(httpSession);
    }

    @Bean
    public DefaultAuthorizationCodeTokenResponseClient customAuthorizationCodeTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient client = new DefaultAuthorizationCodeTokenResponseClient();
        client.setRequestEntityConverter(new CustomRequestEntityConverter()::convert);
        return client;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsServiceImpl userDetailsServiceImpl) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // 개발 단계에서는 CSRF 보호 비활성화
                .securityContext(securityContext -> securityContext
                        .requireExplicitSave(false)
                        .securityContextRepository(new HttpSessionSecurityContextRepository()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/join", "/boards/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/boards").hasRole("USER")
                        .requestMatchers("/myPage/**", "/boards/new").hasRole("USER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOAuth2UserService) // CustomOAuth2UserService 등록
                        )
                        .successHandler(customAuthenticationSuccessHandler()) // 성공 핸들러
                        .failureHandler((request, response, exception) -> { // 실패 핸들러
                            if (exception instanceof OAuth2AuthenticationException oauth2Exception) {
                                String errorCode = oauth2Exception.getError().getErrorCode();
                                if ("additional_info_required".equals(errorCode)) { // 추가 정보 입력 필요
                                    response.sendRedirect("/additional-info");
                                    return;
                                }
                            }
                            // 기타 오류 처리
                            response.sendRedirect("/login?error");
                        })
                )

                .formLogin(form -> form
                        .loginPage("/login") // 로그인 페이지 경로
                        .defaultSuccessUrl("/") // 로그인 성공 기본
                        .successHandler(new SavedRequestAwareAuthenticationSuccessHandler())
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


    @Bean
    public StrictHttpFirewall strictHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true); // "/" 허용
        firewall.setAllowUrlEncodedDoubleSlash(true); // "//" 허용
        return firewall;
    }

}
