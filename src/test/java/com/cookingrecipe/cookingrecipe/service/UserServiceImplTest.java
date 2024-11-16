package com.cookingrecipe.cookingrecipe.service;

import com.cookingrecipe.cookingrecipe.domain.User;
import com.cookingrecipe.cookingrecipe.dto.UserSignupDto;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
import com.cookingrecipe.cookingrecipe.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") // "test" 프로파일을 활성화하여 application-test.yml 사용
@Transactional
class UserServiceImplTest {


    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;


    // 회원가입 - 아이디 중복 O
    @Test
    public void isDuplicatedId() {
        //given
        UserSignupDto userSignupDto = UserSignupDto.builder()
                .loginId("testerA")
                .nickname("nicknameA")
                .password("!tester")
                .email("test@example.com")
                .number("010-1111-1111")
                .birth(LocalDate.of(1998,7,2))
                .build();

        User savedUser = userService.join(userSignupDto);

        //when
        boolean result = userService.isLoginIdDuplicated("testerA");

        //then
        assertThat(result).isTrue();
    }


    // 회원가입 - 아이디 중복 X
    @Test
    public void isNotDuplicatedId() {
        //given
        UserSignupDto userSignupDto = UserSignupDto.builder()
                .loginId("testerA")
                .nickname("nicknameA")
                .password("!tester")
                .email("test@example.com")
                .number("010-1111-1111")
                .birth(LocalDate.of(1998,7,2))
                .build();

        User savedUser = userService.join(userSignupDto);

        //when
        boolean result = userService.isLoginIdDuplicated("testerB");

        //then
        assertThat(result).isFalse();
    }


    // 로그인 사용자 - 비밀번호 변경
    @Test
    public void updatePassword() {
        //given
        UserSignupDto userSignupDto = UserSignupDto.builder()
                .loginId("testerA")
                .nickname("nicknameA")
                .password("!tester")
                .email("test@example.com")
                .number("010-1111-1111")
                .birth(LocalDate.of(1998,7,2))
                .build();

        User savedUser = userService.join(userSignupDto);
        Long savedId = savedUser.getId();

        //when
        String currentPassword = "!tester";
        String newPassword = "@tester";
        String confirmPassword = "!tester";
        userService.updatePassword(savedId, currentPassword, newPassword, confirmPassword);

        //then
        User updatedUser = userService.findById(savedId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        assertThat(passwordEncoder.matches(newPassword, updatedUser.getPassword())).isTrue();
    }


    // 로그인 사용자 - 잘못된 현재 비밀번호 입력
    @Test
    public void updatePasswordWrong() {
        //given
        UserSignupDto userSignupDto = UserSignupDto.builder()
                .loginId("testerA")
                .nickname("nicknameA")
                .password("!tester")
                .email("test@example.com")
                .number("010-1111-1111")
                .birth(LocalDate.of(1998,7,2))
                .build();

        User savedUser = userService.join(userSignupDto);
        Long savedId = savedUser.getId();

        //when
        String wrongPassword = "$tester";
        String newPassword = "@tester";
        String confirmPassword = "@tester";

        //then
        org.junit.jupiter.api.Assertions.assertThrows(BadRequestException.class, () -> {
            userService.updatePassword(savedId, wrongPassword, newPassword, confirmPassword);
        });
    }


    // 로그아웃 사용자 - 번호, 생일로 로그인 아이디 찾기
    @Test
    public void findLoginIdByNumberAndBirth() {
        //given
        UserSignupDto userSignupDto = UserSignupDto.builder()
                .loginId("testerA")
                .nickname("nicknameA")
                .password("!tester")
                .email("test@example.com")
                .number("010-1111-1111")
                .birth(LocalDate.of(1998,7,2))
                .build();

        User savedUser = userService.join(userSignupDto);
        Long savedId = savedUser.getId();

        //when
        String findId = userService.findLoginIdByNumberAndBirth(savedUser.getNumber(), savedUser.getBirth());

        //then
        assertThat(findId).isEqualTo(savedUser.getLoginId());
    }


    // 로그아웃 사용자 - 잘못된 번호로 로그인 아이디 찾기
    @Test
    public void findLoginIdByNumberAndBirthWrong() {
        //given
        UserSignupDto userSignupDto = UserSignupDto.builder()
                .loginId("testerA")
                .nickname("nicknameA")
                .password("!tester")
                .email("test@example.com")
                .number("010-1111-1111")
                .birth(LocalDate.of(1998,7,2))
                .build();

        User savedUser = userService.join(userSignupDto);
        Long savedId = savedUser.getId();

        //when
        String wrongNumber = "010-3333-3333";

        //then
        org.junit.jupiter.api.Assertions.assertThrows(UserNotFoundException.class, () -> {
            String findId = userService.findLoginIdByNumberAndBirth(wrongNumber, savedUser.getBirth());
        });
    }


    // 로그아웃 사용자 - 비밀번호 찾기, 재설정
    @Test
    public void findPassword() {
        //given
        UserSignupDto userSignupDto = UserSignupDto.builder()
                .loginId("testerA")
                .nickname("nicknameA")
                .password("!tester")
                .email("test@example.com")
                .number("010-1111-1111")
                .birth(LocalDate.of(1998,7,2))
                .build();

        User savedUser = userService.join(userSignupDto);
        Long savedId = savedUser.getId();

        //when
        String tempPassword = userService.findPassword(savedUser.getLoginId(), savedUser.getNumber(), savedUser.getBirth());
        String encodedPassword = passwordEncoder.encode(tempPassword);

        //then
        assertThat(tempPassword).isNotEqualTo("!tester");

        User updatedUser = userService.findById(savedId).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        assertThat(passwordEncoder.matches(tempPassword, updatedUser.getPassword())).isTrue();

    }


    // 회원 탈퇴
    @Test
    public void deleteUser_Success() {
        //given
        UserSignupDto userSignupDto = UserSignupDto.builder()
                .loginId("testerA")
                .nickname("nicknameA")
                .password("!tester")
                .email("test@example.com")
                .number("010-1111-1111")
                .birth(LocalDate.of(1998,7,2))
                .build();

        User savedUser = userService.join(userSignupDto);
        Long savedId = savedUser.getId();

        //when
        userService.deleteUser(savedId, savedUser.getPassword());

        //then: 삭제된 유저를 조회했을 때 존재하지 않아야 함
        org.junit.jupiter.api.Assertions.assertThrows(UserNotFoundException.class, () -> {
            userService.findById(savedId)
                    .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));
        });
    }

}