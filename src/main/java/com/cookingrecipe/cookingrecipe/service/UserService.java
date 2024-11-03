package com.cookingrecipe.cookingrecipe.service;

import com.cookingrecipe.cookingrecipe.domain.Birth;
import com.cookingrecipe.cookingrecipe.domain.User;
import com.cookingrecipe.cookingrecipe.dto.UserSignupDto;
import com.cookingrecipe.cookingrecipe.dto.UserUpdateDto;

import java.util.Optional;

public interface UserService {

    // User Entity 생성
    User joinEntity(User user);

    // UserSignupDto 사용한 회원 가입
    User join(UserSignupDto userSignupDto);

    // 회원 가입 시 아이디 중복 검사
    boolean isDuplicatedId(String loginId);

    // 회원 가입 시 이메일 중복 검사
    boolean isDuplicatedEmail(String email);

    // 시스템 ID를 이용하여 회원 조회
    Optional<User> findById(Long id);

    // 회원 정보 변경
    void updateUser(Long id, UserUpdateDto userUpdateDto);

    // 비밀번호 변경
    void updatePassword(Long id, String currentPassword, String newPassword);

    // 아이디 찾기 - 이메일, 전화번호 사용
    String findLoginIdByNumberAndBirth(String number, Birth birth);

    // 회원 탈퇴
    void deleteUser(Long id);

}
