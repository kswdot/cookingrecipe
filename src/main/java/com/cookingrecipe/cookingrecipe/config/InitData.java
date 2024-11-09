package com.cookingrecipe.cookingrecipe.config;

import com.cookingrecipe.cookingrecipe.dto.UserSignupDto;
import com.cookingrecipe.cookingrecipe.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Configuration
@Transactional
@RequiredArgsConstructor
public class InitData {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        createUser("tester1", "테스터1", "@tester1111",
                "tester1@gmail.com", "010-1111-1111", LocalDate.parse("1998-07-02"));
        createUser("tester2", "테스터2", "@tester2222",
                "tester2@gmail.com", "010-2222-2222", LocalDate.parse("1998-07-02"));
    }

    private void createUser(String loginId, String nickname, String password, String email, String number, LocalDate birth) {

        if (!userService.isDuplicatedId(loginId) && !userService.isDuplicatedEmail(email)) {

            UserSignupDto userSignupDto = UserSignupDto.builder()
                    .loginId(loginId)
                    .nickname(nickname)
                    .password(password)
                    .email(email)
                    .number(number)
                    .birth(birth)
                    .build();

            userService.join(userSignupDto);
        }
    }

}
