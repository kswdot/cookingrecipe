package com.cookingrecipe.cookingrecipe.service;

import com.cookingrecipe.cookingrecipe.domain.Role;
import com.cookingrecipe.cookingrecipe.domain.User;
import com.cookingrecipe.cookingrecipe.dto.UserSignupDto;
import com.cookingrecipe.cookingrecipe.dto.UserUpdateDto;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
import com.cookingrecipe.cookingrecipe.exception.UserNotFoundException;
import com.cookingrecipe.cookingrecipe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // User Entity 생성
    @Override
    public User joinEntity(User user) {
        return userRepository.save(user);
    }

    // UserSignupDto 사용해 회원 가입
    @Override
    public User join(UserSignupDto userSignupDto) {

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userSignupDto.getPassword());

        User user = User.builder()
                .loginId(userSignupDto.getLoginId())
                .nickname(userSignupDto.getNickname())
                .password(encodedPassword)
                .email(userSignupDto.getEmail())
                .number(userSignupDto.getNumber())
                .birth(userSignupDto.getBirth())
                .role(Role.ROLE_USER)
                .build();

        // User 엔티티 저장
        joinEntity(user);


        return user;
    }

    // 회원 가입 시 아이디 중복 검사 - 이미 존재하는 경우 true / 그렇지 않은 경우 false 반환
    @Override
    public boolean isDuplicatedId(String loginId) {
        return userRepository.findByLoginId(loginId).isPresent();
    }

    // 회원 가입 시 이메일 중복 검사 - 이미 존재하는 경우 true / 그렇지 않은 경우 false 반환
    @Override
    public boolean isDuplicatedEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // 시스템 ID를 이용하여 회원 조회
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }

    // 회원 정보 변경
    @Override
    public void updateUser(Long id, UserUpdateDto userUpdateDto) {
        User user = findById(id)
                .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));

        user.updateUser(userUpdateDto.getNickname(), userUpdateDto.getEmail(), userUpdateDto.getNumber());
    }

    // 비밀번호 변경
    @Override
    public void updatePassword(Long id, String currentPassword, String newPassword, String confirmPassword) {
        User user = findById(id)
                .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadRequestException("현재 비밀번호가 올바르지 않습니다.");
        }

        checkNewPassword(newPassword, confirmPassword);

        // 새로운 비밀번호 암호화 후 변경
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.updatePassword(encodedNewPassword);
    }

    // 비밀번호 확인
    public void checkNewPassword(String newPassword, String confirmPassword) {

        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("비밀번호 확인이 일치하지 않습니다.");
        }
    }

    // 아이디 찾기 - 이메일, 전화번호 사용
    @Override
    public String findLoginIdByNumberAndBirth(String number, LocalDate birth) {
        return userRepository.findByNumberAndBirth(number, birth)
                .map(User::getLoginId) // 호출한 쪽에서 exception 다루도록 설정
                .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다. 입력한 정보를 다시 확인해주세요."));
    }

    // 회원 탈퇴
    @Override
    public void deleteUser(Long id) {
        User user = findById(id)
                .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));

        userRepository.delete(user);
    }

}
