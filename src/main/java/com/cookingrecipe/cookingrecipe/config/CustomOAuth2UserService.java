package com.cookingrecipe.cookingrecipe.config;

import com.cookingrecipe.cookingrecipe.domain.Role;
import com.cookingrecipe.cookingrecipe.domain.User;
import com.cookingrecipe.cookingrecipe.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 소셜 로그인 제공자 (카카오, 구글 등) 식별
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if ("kakao".equals(registrationId)) {
            // 카카오 사용자 정보 처리 및 User 엔티티 생성/업데이트
            User userEntity = processKakaoUser(oAuth2User.getAttributes());

            // Spring Security 인증 컨텍스트에 유저 정보 저장
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userEntity, null, Collections.singleton(new SimpleGrantedAuthority(userEntity.getRole().name()))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // SecurityContext를 세션에 저장
            httpSession.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            httpSession.setAttribute("user", userEntity);


            // CustomOAuth2User 생성 및 additional-info 닉네임 반영
            CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2User);
            customOAuth2User.setCustomNickname(userEntity.getNickname()); // 추가 정보에서 가져온 닉네임만 사용


            return customOAuth2User;
        } else {
            throw new OAuth2AuthenticationException(new OAuth2Error("unsupported_provider"),
                    "지원하지 않는 소셜 회원가입입니다 : " + registrationId);
        }
    }


    private User processKakaoUser(Map<String, Object> attributes) {
        System.out.println("[DEBUG] Processing Kakao user with attributes: " + attributes);

        Long kakaoId = Long.parseLong(attributes.get("id").toString());

        // 사용자 정보 파싱
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = (profile != null) ? (String) profile.getOrDefault("nickname", "사용자") : "사용자";

        // 사용자 DB에 저장 또는 조회
        User userEntity = userRepository.findByLoginId("kakao-" + kakaoId)
                .orElseGet(() -> {
                    log.info("[DEBUG] User not found, saving new user with Kakao ID: " + kakaoId);
                    return saveUser("kakao-" + kakaoId, nickname);
                });

        // 추가 정보 입력 필요 시 세션에 사용자 정보 저장
        if (userEntity.getEmail().isEmpty() || userEntity.getNumber().isEmpty() || userEntity.getBirth() == null) {
            httpSession.setAttribute("additional_info_required", true);
        } else {
            httpSession.setAttribute("additional_info_required", false);
        }


        return userEntity;
    }


    private User saveUser(String loginId, String nickname) {
        User user = User.builder()
                .loginId(loginId)
                .nickname(nickname)
                .email("") // 이메일 비워둠
                .number("") // 전화번호 비워둠
                .birth(null) // 생년월일 비워둠
                .role(Role.ROLE_USER) // 기본 역할
                .build();
        return userRepository.save(user);
    }
}


