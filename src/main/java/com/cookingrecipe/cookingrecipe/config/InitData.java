package com.cookingrecipe.cookingrecipe.config;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.domain.User;
import com.cookingrecipe.cookingrecipe.dto.BoardSaveDto;
import com.cookingrecipe.cookingrecipe.dto.UserSignupDto;
import com.cookingrecipe.cookingrecipe.exception.UserNotFoundException;
import com.cookingrecipe.cookingrecipe.service.BoardService;
import com.cookingrecipe.cookingrecipe.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

@Configuration
@Transactional
@RequiredArgsConstructor
public class InitData {

    private final UserService userService;
    private final BoardService boardService;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        User user1 = createUser("tester1", "테스터1", "@tester1111",
                "tester1@gmail.com", "010-1111-1111", LocalDate.parse("2024-11-11"));

        User user2 = createUser("tester2", "테스터2", "@tester2222",
                "tester2@gmail.com", "010-2222-2222", LocalDate.parse("1998-07-02"));

        User user3 = createUser("tester3", "테스터3", "@tester3333",
                "tester3@gmail.com", "010-3333-3333", LocalDate.parse("2020-11-18"));

        createBoard("1번 레시피", "감자, 고구마, 토마토", "1번 레시피 : 감자, 고구마, 토마토",
                "양식","src/main/resources/static/images/cat.jpg", user1);

        createBoard("2번 레시피", "우유, 스파게티, 닭고기", "2번 레시피 : 우유, 스파게티, 닭고기",
                "중식","src/main/resources/static/images/dog.jpg", user2);

        createBoard("3번 레시피", "김치, 다진 마늘, 소고기, 양파, 파", "3번 레시피 : 김치, 다진 마늘, 소고기, 양파, 파",
                "한식","src/main/resources/static/images/rabbit.jpg", user3);
    }

    private User createUser(String loginId, String nickname, String password, String email, String number, LocalDate birth) {

        if (!userService.isLoginIdDuplicated(loginId) && !userService.isEmailDuplicated(email)) {

            UserSignupDto userSignupDto = UserSignupDto.builder()
                    .loginId(loginId)
                    .nickname(nickname)
                    .password(password)
                    .email(email)
                    .number(number)
                    .birth(birth)
                    .build();

            userService.join(userSignupDto);
        }

        return userService.findByLoginId(loginId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 없습니다."));

    }

    private void createBoard(String title, String ingredient, String content, String category, String imagePath, User user) {

        try {
            // Mock 이미지 생성
            MockMultipartFile mockImage = createMockImage(imagePath);

            BoardSaveDto boardSaveDto = BoardSaveDto.builder()
                    .title(title)
                    .ingredient(ingredient)
                    .category(category)
                    .content(content)
                    .build();

            boardService.save(boardSaveDto, Collections.singletonList(mockImage), user.toCustomUserDetails());
        } catch (Exception e) {
            throw new IllegalStateException("게시글 생성 중 오류 발생", e);
        }

    }

    private MockMultipartFile createMockImage(String filePath) {
        try {
            Path path = Paths.get(filePath);
            byte[] content = Files.readAllBytes(path); // 파일 내용을 byte 배열로 읽음
            String fileName = path.getFileName().toString(); // 파일 이름 추출
            return new MockMultipartFile("image", fileName, "image/jpeg", content);
        } catch (Exception e) {
            throw new IllegalStateException("Mock 이미지 생성 중 오류 발생", e);
        }
    }

}
