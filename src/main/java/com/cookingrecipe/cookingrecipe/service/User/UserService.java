package com.cookingrecipe.cookingrecipe.service.User;

import com.cookingrecipe.cookingrecipe.domain.User;
import com.cookingrecipe.cookingrecipe.dto.BoardWithImageDto;
import com.cookingrecipe.cookingrecipe.dto.SocialSignupDto;
import com.cookingrecipe.cookingrecipe.dto.UserSignupDto;
import com.cookingrecipe.cookingrecipe.dto.UserUpdateDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public interface UserService {

    // 일반 회원 가입 - UserSignupDto 사용
    User join(UserSignupDto userSignupDto);

    // 관리자 회원 가입 - UserSignupDto 사용
    User joinAdmin(UserSignupDto userSignupDto);

    // 소셜 간편 회원 가입 - SocialSignupDto 사용
    User joinBySocial(SocialSignupDto socialSignupDto, User user);

    // 회원 가입 - 자동 로그인
    void autoLogin(String loginId, String rawPassword);

    // 회원 가입 - 아이디 중복 검사
    boolean isLoginIdDuplicated(String loginId);

    // 회원 가입 - 이메일 중복 검사
    boolean isEmailDuplicated(String email);

    // 회원 조회 - 시스템 ID
    Optional<User> findById(Long userId);

    // 회원 조회 - 로그인 ID
    Optional<User> findByLoginId(String loginId);

    // 마이 페이지 - 회원 정보 변경
    void updateUser(Long userId, UserUpdateDto userUpdateDto);

    // 마이 페이지 - 비밀번호 변경
    void updatePassword(Long userId, String currentPassword, String newPassword, String confirmPassword);

    // 마이 페이지 - 내가 쓴 글 조회
    List<BoardWithImageDto> findByUserId(Long userId);

    // 마이 페이지 - 북마크한 글 조회
    List<BoardWithImageDto> findBookmarkedRecipeByUser(Long userId);

    // 아이디 찾기 - 이메일, 전화번호
    String findLoginIdByNumberAndBirth(String number, LocalDate birth);

    // 비밀번호 재발급 - 로그인 ID, 이메일, 전화번호
    String findPassword(String LoginId, String number, LocalDate birth);

    // 일반 회원 탈퇴
    void deleteUser(Long userId, String enteredPassword);

    // ADMIN - 모든 회원 조회
    List<User> findAll();

    // ADMIN - 회원 삭제
    void deleteById(Long userId);
}
