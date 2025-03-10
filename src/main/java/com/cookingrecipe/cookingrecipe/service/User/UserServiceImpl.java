package com.cookingrecipe.cookingrecipe.service.User;

import com.cookingrecipe.cookingrecipe.domain.Board.Board;
import com.cookingrecipe.cookingrecipe.domain.User.Role;
import com.cookingrecipe.cookingrecipe.domain.User.User;
import com.cookingrecipe.cookingrecipe.dto.Board.BoardDto;
import com.cookingrecipe.cookingrecipe.dto.User.SocialSignupDto;
import com.cookingrecipe.cookingrecipe.dto.User.UserSignupDto;
import com.cookingrecipe.cookingrecipe.dto.User.UserUpdateDto;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
import com.cookingrecipe.cookingrecipe.exception.UserNotFoundException;
import com.cookingrecipe.cookingrecipe.repository.Board.BoardRepositoryCustom;
import com.cookingrecipe.cookingrecipe.repository.UserRepository;
import com.cookingrecipe.cookingrecipe.service.Board.BoardMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BoardRepositoryCustom boardRepositoryCustom;
    private final BoardMapper boardMapper;
    private final PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;


    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    private User createUser(UserSignupDto userSignupDto, Role role) {
        String encodedPassword = passwordEncoder.encode(userSignupDto.getPassword());

        return User.builder()
                .loginId(userSignupDto.getLoginId())
                .nickname(userSignupDto.getNickname())
                .password(encodedPassword)
                .email(userSignupDto.getEmail())
                .number(userSignupDto.getNumber())
                .birth(userSignupDto.getBirth())
                .role(role)
                .build();
    }

    // 일반 회원 가입 - UserSignupDto 사용
    @Override
    public User join(UserSignupDto userSignupDto) {
        return userRepository.save(createUser(userSignupDto, Role.ROLE_USER));
    }


    // 관리자 회원 가입
    @Override
    public User joinAdmin(UserSignupDto userSignupDto) {
        return userRepository.save(createUser(userSignupDto, Role.ROLE_ADMIN));
    }


    // 소셜 간편 회원가입 - SocialSignupDto 사용
    @Override
    public User joinBySocial(SocialSignupDto socialSignupDto, User user) {
        user.addUserInfo(
                socialSignupDto.getEmail(),
                socialSignupDto.getNumber(),
                socialSignupDto.getBirth(),
                socialSignupDto.getNickname()
        );

        return userRepository.save(user);
    }


    // 자동 로그인
    @Override
    public void autoLogin(String loginId, String password) {

        // 인증 토큰 생성
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginId, password);

        try {
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            Authentication contextAuth = SecurityContextHolder.getContext().getAuthentication();
        } catch (Exception e) {
            throw e;
        }
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
    public List<BoardDto> findByUserId(Long userId) {
        List<Board> boards = boardRepositoryCustom.findByUserId(userId);

        return boardMapper.mapToBoardDto(boards);
    }


    // 마이 페이지 - 북마크한 글 조회
    @Override
    public List<BoardDto> findBookmarkedRecipeByUser(Long userId) {
        List<Board> boards = boardRepositoryCustom.findBookmarkedRecipeByUser(userId);

        return boardMapper.mapToBoardDto(boards);
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


    // 일반 회원 탈퇴
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


    // 모든 회원 조회
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }


    // 관리자 - 회원 삭제
    @Override
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }

}
