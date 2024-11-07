package com.cookingrecipe.cookingrecipe.controller;

import com.cookingrecipe.cookingrecipe.dto.UserLoginDto;
import com.cookingrecipe.cookingrecipe.dto.UserSignupDto;
import com.cookingrecipe.cookingrecipe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;


    @GetMapping("/join")
    public String joinForm(@ModelAttribute("form") UserSignupDto userSignupDto) {
        return "user/joinForm";
    }

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


    @GetMapping("/login")
    public String loginForm(@ModelAttribute("form") UserLoginDto userLoginDto) {
        return "user/loginForm";
    }

    @GetMapping("/{userId}/myPage")
    public String myPage(Model model, @PathVariable("userId") String userId) {
        return "user/myPageForm";
    }
}
