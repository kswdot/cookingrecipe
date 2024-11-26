package com.cookingrecipe.cookingrecipe.service;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.domain.Role;
import com.cookingrecipe.cookingrecipe.domain.User;
import com.cookingrecipe.cookingrecipe.dto.BoardWithImageDto;
import com.cookingrecipe.cookingrecipe.dto.UserSignupDto;
import com.cookingrecipe.cookingrecipe.dto.UserUpdateDto;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
import com.cookingrecipe.cookingrecipe.exception.UserNotFoundException;
import com.cookingrecipe.cookingrecipe.repository.BoardRepositoryCustom;
import com.cookingrecipe.cookingrecipe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Qualifier;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BoardRepositoryCustom boardRepositoryCustom;
    private final BoardMapper boardMapper;
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
    public boolean isLoginIdDuplicated(String loginId) {
        return userRepository.findByLoginId(loginId).isPresent();
    }


    // 회원 가입 시 이메일 중복 검사 - 이미 존재하는 경우 true / 그렇지 않은 경우 false 반환
    @Override
    public boolean isEmailDuplicated(String email) {
        return userRepository.findByEmail(email).isPresent();
    }


    // 시스템 ID를 이용하여 회원 조회
    @Override
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    // 로그인 ID를 이용하여 회원 조회
    @Override
    public Optional<User> findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }


    // 회원 정보 변경
    @Override
    public void updateUser(Long userId, UserUpdateDto userUpdateDto) {
        User user = findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));

        user.updateUser(userUpdateDto.getNickname(), userUpdateDto.getEmail(), userUpdateDto.getNumber());
    }


    // 비밀번호 변경
    @Override
    public void updatePassword(Long userId, String currentPassword, String newPassword, String confirmPassword) {
        User user = findById(userId)
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
    private void checkNewPassword(String newPassword, String confirmPassword) {

        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("비밀번호 확인이 일치하지 않습니다.");
        }
    }


    // 마이 페이지 - 내가 쓴 글 조회
    @Override
    public List<BoardWithImageDto> findByUserId(Long userId) {
        List<Board> boards = boardRepositoryCustom.findByUserId(userId);

        return boardMapper.findBoardsWithMainImages(boards);
    }


    // 마이 페이지 - 북마크한 글 조회
    @Override
    public List<BoardWithImageDto> findBookmarkedRecipeByUser(Long userId) {
        List<Board> boards = boardRepositoryCustom.findBookmarkedRecipeByUser(userId);

        return boardMapper.findBoardsWithMainImages(boards);
    }


    // 아이디 찾기 - 이메일, 전화번호 사용
    @Override
    public String findLoginIdByNumberAndBirth(String number, LocalDate birth) {
        return userRepository.findByNumberAndBirth(number, birth)
                .map(User::getLoginId) // 호출한 쪽에서 exception 다루도록 설정
                .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다. 입력한 정보를 다시 확인해주세요."));
    }

    // 비밀번호 찾기(재발급)
    @Override
    public String findPassword(String LoginId, String number, LocalDate birth) {

        User findUser = userRepository.findByNumberAndBirth(number, birth)
                .filter(u -> u.getLoginId().equals(LoginId))
                .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다. 입력한 정보를 다시 확인해주세요"));

        // 임시 비밀번호 생성
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);

        // 임시 비밀번호 암호화 후 DB 업데이트
        String encodedPassword = passwordEncoder.encode(tempPassword);
        findUser.updatePassword(encodedPassword);

        return tempPassword;
    }


    // 회원 탈퇴
    @Override
    public void deleteUser(Long userId, String enteredPassword) {

        User user = findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(enteredPassword, user.getPassword())) {
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
        }

        userRepository.delete(user);
    }



}
