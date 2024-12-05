package com.cookingrecipe.cookingrecipe.controller.api;

import com.cookingrecipe.cookingrecipe.domain.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.domain.User;
import com.cookingrecipe.cookingrecipe.dto.*;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
import com.cookingrecipe.cookingrecipe.exception.UserNotFoundException;
import com.cookingrecipe.cookingrecipe.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
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
public class UserApiController {


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
            return ResponseEntity.badRequest().body(Map.of("error","이미 사용 중인 아이디입니다."));
        }

        // 이메일 중복 검사
        if (userService.isEmailDuplicated(userSignupDto.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "이미 사용 중인 이메일입니다."));
        }

        // 회원가입 성공 로직
        try {
            User user = userService.join(userSignupDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입 완료!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error","회원가입 중 문제가 발생하였습니다. 다시 시도해주세요."));
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
                    .body(Map.of("error","해당 유저를 찾을 수 없습니다. 입력한 정보를 다시 확인해주세요"));
        }
    }


    // 비밀번호 재발급
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
                    .body(Map.of("message","해당 정보를 찾을 수 없습니다. 입력한 정보를 다시 확인해주세요."));
        }
    }


    // 마이페이지 - 회원 정보 확인
    @GetMapping("/info")
    public ResponseEntity<?> info(@AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userService.findById(userDetails.getId())
                .orElseThrow(() -> new UserNotFoundException("해당 사용자의 정보를 찾을 수 없습니다"));

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "loginId", user.getLoginId(),
                "nickname", user.getNickname(),
                "email", user.getEmail(),
                "number", user.getNumber(),
                "birth", user.getBirth()
        ));
    }


    // 마이페이지 - 회원 정보 수정
    @PatchMapping("/info")
    public ResponseEntity<?> info(@AuthenticationPrincipal CustomUserDetails userDetails,
                                   @RequestBody @Validated UserUpdateDto userUpdateDto,
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

        userService.updateUser(userDetails.getId(), userUpdateDto);

        return ResponseEntity.ok().body(Map.of("message","회원 정보가 변경 완료되었습니다."));
    }


    // 마이페이지 - 비밀번호 변경
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @Validated @RequestBody PasswordUpdateDto passwordUpdateDto,
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

        userService.updatePassword(userDetails.getId(), passwordUpdateDto.getCurrentPassword(),
                passwordUpdateDto.getNewPassword(), passwordUpdateDto.getConfirmPassword());


        return ResponseEntity.ok().body(Map.of("message","비밀번호가 성공적으로 변경되었습니다."));
    }


    // 마이페이지 - 내가 쓴 글 조회
    @GetMapping("/boardList")
    public ResponseEntity<?> boardList(@AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
            List<BoardWithImageDto> boards = userService.findByUserId(userDetails.getId());

            return ResponseEntity.ok(boards);
        } catch (Exception e){

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "게시글을 조회하던 중 문제가 발생했습니다"));
        }
    }


    // 마이페이지 - 북마크한 글 조회
    @GetMapping("/bookmark")
    public ResponseEntity<?> bookmark(@AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
            List<BoardWithImageDto> boards = userService.findBookmarkedRecipeByUser(userDetails.getId());

            return ResponseEntity.ok().body(boards);
        } catch (Exception e){

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "북마크를 조회하던 중 문제가 발생했습니다."));
        }
    }


    // 마이페이지 - 회원 탈퇴
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails,
                           @RequestBody Map<String, String> requestData,
                           HttpServletRequest request) {

        String enteredPassword = requestData.get("password");

        try {
            // 서비스 로직에서 비밀번호 검증 포함
            userService.deleteUser(userDetails.getId(), enteredPassword);

            // 로그아웃 처리
            request.logout();

            return ResponseEntity.ok().body(Map.of("message", "회원 탈퇴가 완료되었습니다"));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "올바른 비밀번호를 입력하세요"));
        } catch (ServletException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "에러가 발생했습니다. 다시 시도해주세요."));
        }
    }




}
