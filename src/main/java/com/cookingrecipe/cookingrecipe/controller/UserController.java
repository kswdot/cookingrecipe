package com.cookingrecipe.cookingrecipe.controller;

import com.cookingrecipe.cookingrecipe.domain.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.domain.User;
import com.cookingrecipe.cookingrecipe.dto.PasswordUpdateDto;
import com.cookingrecipe.cookingrecipe.dto.UserLoginDto;
import com.cookingrecipe.cookingrecipe.dto.UserSignupDto;
import com.cookingrecipe.cookingrecipe.dto.UserUpdateDto;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
import com.cookingrecipe.cookingrecipe.exception.UserNotFoundException;
import com.cookingrecipe.cookingrecipe.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;


    // 회원가입 폼 제공
    @GetMapping("/join")
    public String joinForm(@ModelAttribute("form") UserSignupDto userSignupDto) {
        return "user/joinForm";
    }

    // 회원가입
    @PostMapping("/join")
    public String join(@Validated @ModelAttribute("form") UserSignupDto userSignupDto,
                       BindingResult bindingResult, Model model) {

        // 유효성 검사
        if (bindingResult.hasErrors()) {
            return "user/joinForm";
        }

        // 로그인 ID 중복 검사
        if (userService.isDuplicatedId(userSignupDto.getLoginId())) {
            bindingResult.rejectValue("loginId", "duplicatedLoginId");
        }

        // 이메일 중복 검사
        if (userService.isDuplicatedEmail(userSignupDto.getEmail())) {
            bindingResult.rejectValue("email", "duplicatedEmail");
        }

        // 유효성 검사에서 오류가 있으면 회원가입 페이지로 다시 이동
        if (bindingResult.hasErrors()) {
            return "user/joinForm";
        }

        // 회원가입 성공 로직
        try {
            userService.join(userSignupDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "회원가입 중 문제가 발생했습니다. 다시 시도해주세요.");
            return "user/joinForm";
        }

        // 회원 가입 후 로그인 페이지로 이동
        // 추후 회원 가입 후 자동 로그인이 되어 메인 페이지로 이동하게 리팩토링 예정
        return "redirect:/login";
    }

    // 로그인 폼 - 로그인 과정은 Spring Security 관여
    @GetMapping("/login")
    public String loginForm(@ModelAttribute("form") UserLoginDto userLoginDto) {
        return "user/loginForm";
    }


    // 마이페이지
    @GetMapping("/myPage")
    public String myPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("user", userDetails);
        return "user/myPage";
    }

    // 마이페이지 - 회원 정보 확인
    @GetMapping("/myPage/info")
    public String myPageInfo(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        User user = userService.findById(userDetails.getId())
                .orElseThrow(() -> new UserNotFoundException("해당 사용자의 정보를 찾을 수 없습니다"));

        model.addAttribute("user", user);
        return "user/myPageInfo";
    }

    // 마이페이지 - 회원 정보 수정 폼
    @GetMapping("/myPage/info/edit")
    public String myPageInfoUpdateForm(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       Model model) {
        UserUpdateDto userUpdateDto = new UserUpdateDto(
                userDetails.getUsername(),
                userDetails.getNickname(),
                userDetails.getEmail(),
                userDetails.getNumber(),
                userDetails.getBirth()
        );

        model.addAttribute("user", userDetails);
        model.addAttribute("form", userUpdateDto);

        return "user/myPageInfoEdit";
    }

    // 마이페이지 - 회원 정보 수정
    @PatchMapping("/myPage/info")
    public String myPageInfoUpdate(@AuthenticationPrincipal CustomUserDetails userDetails,
                                   @Validated @ModelAttribute("form") UserUpdateDto userUpdateDto,
                                   BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "user/myPageInfoEdit";
        }


        userService.updateUser(userDetails.getId(), userUpdateDto);
        redirectAttributes.addFlashAttribute("successMessage", "회원 정보가 변경되었습니다.");

        return "redirect:/myPage/info";
    }
    
    // 마이페이지 - 비밀번호 변경 폼
    @GetMapping("/myPage/info/password")
    public String myPagePasswordForm(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                     Model model) {

        model.addAttribute("user", customUserDetails);
        model.addAttribute("form", new PasswordUpdateDto());

        return "user/myPagePassword";
    }

    // 마이페이지 - 비밀번호 변경
    @PutMapping("/myPage/info/password")
    public String myPageInfoEditPassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @Validated @ModelAttribute("form") PasswordUpdateDto passwordUpdateDto,
                                         BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "user/myPagePassword";
        }

        userService.updatePassword(userDetails.getId(), passwordUpdateDto.getCurrentPassword(),
                passwordUpdateDto.getNewPassword(), passwordUpdateDto.getConfirmPassword());

        redirectAttributes.addFlashAttribute("successMessage", "비밀번호가 변경되었습니다.");

        return "redirect:/myPage/info";
    }

    @GetMapping("/bookmark")
    public String bookMark() {

        return "user/bookmark";
    }

    // 마이페이지 - 회원 탈퇴 폼
    @GetMapping("/myPage/withdraw")
    public String withdrawForm() {

        return "user/withdrawForm";
    }

    // 마이페이지 - 회원 탈퇴
    @PostMapping("/myPage/withdraw")
    public String withdraw(@AuthenticationPrincipal CustomUserDetails userDetails,
                           @RequestParam("password") String enteredPassword,
                           RedirectAttributes redirectAttributes,
                           HttpServletRequest request) {

        if (enteredPassword == null || enteredPassword.isEmpty()) {
            redirectAttributes.addFlashAttribute("passwordError", "비밀번호를 입력해주세요");
            return "user/withdrawForm";
        }

        try {
            userService.deleteUser(userDetails.getId(), enteredPassword);

            // 로그아웃 처리
            request.logout();

            redirectAttributes.addFlashAttribute("successMessage", "회원 탈퇴가 완료되었습니다.");
            return "redirect:/";
        } catch (BadRequestException e) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "/user/withdrawForm";
        } catch (ServletException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "에러가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/myPage/withdraw";
        }
    }
}
