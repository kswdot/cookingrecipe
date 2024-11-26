package com.cookingrecipe.cookingrecipe.controller;

import com.cookingrecipe.cookingrecipe.domain.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.domain.User;
import com.cookingrecipe.cookingrecipe.dto.*;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
import com.cookingrecipe.cookingrecipe.exception.UserNotFoundException;
import com.cookingrecipe.cookingrecipe.service.BoardService;
import com.cookingrecipe.cookingrecipe.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BoardService boardService;


    // 회원가입 폼 제공
    @GetMapping("/join")
    public String joinForm(@ModelAttribute("form") UserSignupDto userSignupDto) {
        return "user/join";
    }


    // 회원가입
    @PostMapping("/join")
    public String join(@Validated @ModelAttribute("form") UserSignupDto userSignupDto,
                       BindingResult bindingResult, Model model) {

        // 유효성 검사
        if (bindingResult.hasErrors()) {
            return "user/join";
        }

        // 로그인 ID 중복 검사
        if (userService.isLoginIdDuplicated(userSignupDto.getLoginId())) {
            bindingResult.rejectValue("loginId", "duplicatedLoginId");
        }

        // 이메일 중복 검사
        if (userService.isEmailDuplicated(userSignupDto.getEmail())) {
            bindingResult.rejectValue("email", "duplicatedEmail");
        }

        // 유효성 검사에서 오류가 있으면 회원가입 페이지로 다시 이동
        if (bindingResult.hasErrors()) {
            return "user/join";
        }

        // 회원가입 성공 로직
        try {
            userService.join(userSignupDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "회원가입 중 문제가 발생했습니다. 다시 시도해주세요.");
            return "user/join";
        }

        // 회원 가입 후 로그인 페이지로 이동
        // 추후 회원 가입 후 자동 로그인이 되어 메인 페이지로 이동하게 리팩토링 예정
        return "redirect:/login";
    }


    // 로그인 폼 - 로그인 과정은 Spring Security 관여
    @GetMapping("/login")
    public String loginForm(@ModelAttribute("form") UserLoginDto userLoginDto) {

        return "user/login";
    }


    // 로그인 아이디 찾기 폼
    @GetMapping("/findLoginId")
    public String findLoginIdForm(@ModelAttribute("form") FindLoginIdDto findLoginIdDto) {
        return "user/findLoginId";
    }


    // 로그인 아이디 찾기
    @PostMapping("/findLoginId")
    public String findLoginId(@ModelAttribute("form") FindLoginIdDto findLoginIdDto,
                              BindingResult bindingResult, Model model) {

        // 유효성 검사 -> 폼으로 이동
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "입력한 정보를 다시 확인해주세요.");
            return "user/findLoginId";
        }

        try {
            // 아이디 찾기 성공 시 모델에 loginId 추가
            String loginId = userService.findLoginIdByNumberAndBirth(findLoginIdDto.getNumber(), findLoginIdDto.getBirth());
            model.addAttribute("loginId", loginId);
            return "user/findLoginId"; // 현재 페이지에 결과 표시
        } catch (UserNotFoundException e) {
            // 유저를 찾지 못한 경우 예외 메시지 추가
            model.addAttribute("errorMessage", "해당 유저를 찾을 수 없습니다. 입력한 정보를 다시 확인해주세요.");
            return "user/findLoginId";
        }
    }


    // 비밀번호 찾기(재발급) 폼
    @GetMapping("/findPassword")
    public String findPasswordForm(@ModelAttribute("form") FindPasswordDto findPasswordDto) {
        return "user/findPassword";
    }


    // 비밀번호 찾기(재발급)
    @PostMapping("/findPassword")
    public String findPassword(@ModelAttribute("form") FindPasswordDto findPasswordDto,
                               BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "입력한 정보를 다시 확인해주세요");
            return "user/findPassword";
        }

        try {
            String tempPassword = userService.findPassword(
                    findPasswordDto.getLoginId(),
                    findPasswordDto.getNumber(),
                    findPasswordDto.getBirth()
            );

            model.addAttribute("successMessage", "임시 비밀번호가 발급되었습니다.");
            model.addAttribute("tempPassword", tempPassword);
            return "user/findPassword";
        } catch (UserNotFoundException e) {
            model.addAttribute("errorMessage", "해당 정보를 찾을 수 없습니다. 입력한 정보를 다시 확인해주세요.");
            return "user/findPassword";
        }
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


    // 마이페이지 - 내가 쓴 글 조회
    @GetMapping("/myPage/boardList")
    public String boardList(@AuthenticationPrincipal CustomUserDetails userDetails,
                            Model model) {

        List<BoardWithImageDto> boards = userService.findByUserId(userDetails.getId());

        model.addAttribute("user", userDetails);
        model.addAttribute("boards", boards);

        return "user/myPageBoardList";
    }


    // 마이페이지 - 북마크한 글 조회
    @GetMapping("/myPage/bookmark")
    public String bookMark(@AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {

        List<BoardWithImageDto> boards = userService.findBookmarkedRecipeByUser(userDetails.getId());

        model.addAttribute("user", userDetails);
        model.addAttribute("boards", boards);

        return "user/myPageBookmark";
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

        try {
            // 서비스 로직에서 비밀번호 검증 포함
            userService.deleteUser(userDetails.getId(), enteredPassword);

            // 로그아웃 처리
            request.logout();

            redirectAttributes.addFlashAttribute("successMessage", "회원 탈퇴가 완료되었습니다.");
            return "redirect:/";
        } catch (BadRequestException e) {
            redirectAttributes.addFlashAttribute("passwordError", "올바른 비밀번호를 입력하세요.");
            return "redirect:/myPage/withdraw";
        } catch (ServletException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "에러가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/myPage/withdraw";
        }


    }
}
