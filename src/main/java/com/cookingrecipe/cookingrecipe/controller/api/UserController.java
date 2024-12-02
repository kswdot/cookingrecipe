package com.cookingrecipe.cookingrecipe.controller.api;

import com.cookingrecipe.cookingrecipe.domain.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.domain.User;
import com.cookingrecipe.cookingrecipe.dto.FindLoginIdDto;
import com.cookingrecipe.cookingrecipe.dto.FindPasswordDto;
import com.cookingrecipe.cookingrecipe.dto.UserSignupDto;
import com.cookingrecipe.cookingrecipe.exception.UserNotFoundException;
import com.cookingrecipe.cookingrecipe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;


    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Validated UserSignupDto userSignupDto,
                                          BindingResult bindingResult) {

        // 유효성 검사
        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> Map.of(
                            "field", error.getField(),
                            "message", error.getDefaultMessage()
                    ))
                    .toList();

            return ResponseEntity.badRequest().body(errorMessages);
        }

        // 로그인 ID 중복 검사
        if (userService.isLoginIdDuplicated(userSignupDto.getLoginId())) {
            return ResponseEntity.badRequest().body("이미 사용 중인 아이디입니다.");
        }

        // 이메일 중복 검사
        if (userService.isEmailDuplicated(userSignupDto.getEmail())) {
            return ResponseEntity.badRequest().body("이미 사용 중인 이메일입니다.");
        }

        // 회원가입 성공 로직
        try {
            User user = userService.join(userSignupDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입 완료!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원가입 중 문제가 발생하였습니다. 다시 시도해주세요.");
        }
    }


    // 로그인 ID 찾기
    @PostMapping("/find-login-id")
    public ResponseEntity<?> findLoginId(@RequestBody @Validated FindLoginIdDto findLoginIdDto,
                                         BindingResult bindingResult) {

        // 유효성 검사
        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> Map.of(
                            "field", error.getField(),
                            "message", error.getDefaultMessage()
                    ))
                    .toList();

            return ResponseEntity.badRequest().body(errorMessages);
        }

        try {
            String loginId = userService.findLoginIdByNumberAndBirth(findLoginIdDto.getNumber(),
                    findLoginIdDto.getBirth());

            return ResponseEntity.ok(Map.of("loginId", loginId));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 유저를 찾을 수 없습니다. 입력한 정보를 다시 확인해주세요");
        }
    }


    // 비밀번호 찾기(재발급)
    @PostMapping("/find-password")
    public ResponseEntity<?> findPassword(@RequestBody @Validated FindPasswordDto findPasswordDto,
                                          BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> Map.of(
                            "field", error.getField(),
                            "message", error.getDefaultMessage()
                    ))
                    .toList();

            return ResponseEntity.badRequest().body(errorMessages);
        }

        try {
            String tempPassword = userService.findPassword(
                    findPasswordDto.getLoginId(),
                    findPasswordDto.getNumber(),
                    findPasswordDto.getBirth());

            return ResponseEntity.ok(Map.of(
                    "message", "임시 비밀번호가 발급되었습니다.",
                    "tempPassword", tempPassword
            ));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 정보를 찾을 수 없습니다. 입력한 정보를 다시 확인해주세요.");
        }
    }






}
