package com.cookingrecipe.cookingrecipe.service;

import com.cookingrecipe.cookingrecipe.domain.Birth;
import com.cookingrecipe.cookingrecipe.domain.User;
import com.cookingrecipe.cookingrecipe.dto.UserSignupDto;
import com.cookingrecipe.cookingrecipe.dto.UserUpdateDto;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
import com.cookingrecipe.cookingrecipe.exception.UserNotFoundException;
import com.cookingrecipe.cookingrecipe.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") // "test" 프로파일을 활성화하여 application-test.yml 사용
@Transactional
class UserServiceImplTest {


    @Autowired
    UserService userService;


    @Test
    public void isDuplicatedId() {
        //given
        UserSignupDto userSignupDto = UserSignupDto.builder()
                .loginId("testerA")
                .nickname("nicknameA")
                .password("!tester")
                .email("test@example.com")
                .number("010-1111-1111")
                .birth(new Birth(1998, 7, 2))
                .build();

        User savedUser = userService.join(userSignupDto);

        //when
        boolean result = userService.isDuplicatedId("testerA");

        //then
        assertThat(result).isTrue();
    }


    @Test
    public void isNotDuplicatedId() {
        //given
        UserSignupDto userSignupDto = UserSignupDto.builder()
                .loginId("testerA")
                .nickname("nicknameA")
                .password("!tester")
                .email("test@example.com")
                .number("010-1111-1111")
                .birth(new Birth(1998, 7, 2))
                .build();

        User savedUser = userService.join(userSignupDto);

        //when
        boolean result = userService.isDuplicatedId("testerB");

        //then
        assertThat(result).isFalse();
    }


    @Test
    public void updateUser() {
        //given
        UserSignupDto userSignupDto = UserSignupDto.builder()
                .loginId("testerA")
                .nickname("nicknameA")
                .password("!tester")
                .email("test@example.com")
                .number("010-1111-1111")
                .birth(new Birth(1998, 7, 2))
                .build();

        User savedUser = userService.join(userSignupDto);
        Long savedId = savedUser.getId();

        //when
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .nickname("nicknameB")
                .number("010-2222-222")
                .build();

        userService.updateUser(savedId, userUpdateDto);

        //then
        assertThat(savedUser.getNickname()).isEqualTo("nicknameB");
        assertThat(savedUser.getNumber()).isEqualTo("010-2222-222");
    }


    @Test
    public void updatePassword() {
        //given
        UserSignupDto userSignupDto = UserSignupDto.builder()
                .loginId("testerA")
                .nickname("nicknameA")
                .password("!tester")
                .email("test@example.com")
                .number("010-1111-1111")
                .birth(new Birth(1998, 7, 2))
                .build();

        User savedUser = userService.join(userSignupDto);
        Long savedId = savedUser.getId();

        //when
        String newPassword = "@tester";
        userService.updatePassword(savedId, savedUser.getPassword(),newPassword);

        //then
        assertThat(savedUser.getPassword()).isEqualTo(newPassword);
    }


    @Test
    public void updatePasswordWrong() {
        //given
        UserSignupDto userSignupDto = UserSignupDto.builder()
                .loginId("testerA")
                .nickname("nicknameA")
                .password("!tester")
                .email("test@example.com")
                .number("010-1111-1111")
                .birth(new Birth(1998, 7, 2))
                .build();

        User savedUser = userService.join(userSignupDto);
        Long savedId = savedUser.getId();

        //when
        String wrongPassword = "$tester";
        String newPassword = "@tester";

        //then
        org.junit.jupiter.api.Assertions.assertThrows(BadRequestException.class, () -> {
            userService.updatePassword(savedId, wrongPassword, newPassword);
        });
    }


    @Test
    public void findLoginIdByNumberAndBirth() {
        //given
        UserSignupDto userSignupDto = UserSignupDto.builder()
                .loginId("testerA")
                .nickname("nicknameA")
                .password("!tester")
                .email("test@example.com")
                .number("010-1111-1111")
                .birth(new Birth(1998, 7, 2))
                .build();

        User savedUser = userService.join(userSignupDto);
        Long savedId = savedUser.getId();

        //when
        String findId = userService.findLoginIdByNumberAndBirth(savedUser.getNumber(), savedUser.getBirth());

        //then
        assertThat(findId).isEqualTo(savedUser.getLoginId());
    }


    @Test
    public void findLoginIdByNumberAndBirthWrong() {
        //given
        UserSignupDto userSignupDto = UserSignupDto.builder()
                .loginId("testerA")
                .nickname("nicknameA")
                .password("!tester")
                .email("test@example.com")
                .number("010-1111-1111")
                .birth(new Birth(1998, 7, 2))
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


    @Test
    public void deleteUser_Success() {
        //given
        UserSignupDto userSignupDto = UserSignupDto.builder()
                .loginId("testerA")
                .nickname("nicknameA")
                .password("!tester")
                .email("test@example.com")
                .number("010-1111-1111")
                .birth(new Birth(1998, 7, 2))
                .build();

        User savedUser = userService.join(userSignupDto);
        Long savedId = savedUser.getId();

        //when
        userService.deleteUser(savedId);

        //then: 삭제된 유저를 조회했을 때 존재하지 않아야 함
        org.junit.jupiter.api.Assertions.assertThrows(UserNotFoundException.class, () -> {
            userService.findById(savedId)
                    .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));
        });
    }
}