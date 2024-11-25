package com.cookingrecipe.cookingrecipe.config;

import com.cookingrecipe.cookingrecipe.domain.Category;
import com.cookingrecipe.cookingrecipe.domain.Method;
import com.cookingrecipe.cookingrecipe.domain.User;
import com.cookingrecipe.cookingrecipe.dto.BoardSaveDto;
import com.cookingrecipe.cookingrecipe.dto.InitBoardSaveDto;
import com.cookingrecipe.cookingrecipe.dto.RecipeStepDto;
import com.cookingrecipe.cookingrecipe.dto.UserSignupDto;
import com.cookingrecipe.cookingrecipe.exception.UserNotFoundException;
import com.cookingrecipe.cookingrecipe.service.BoardService;
import com.cookingrecipe.cookingrecipe.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class InitData {

    private final UserService userService;
    private final BoardService boardService;

    @PostConstruct
    @Transactional
    public void init() {
        User user1 = createUser("tester1", "테스터1", "@tester1111",
                "tester1@gmail.com", "010-1111-1111", LocalDate.parse("2024-11-11"));

        User user2 = createUser("tester2", "테스터2", "@tester2222",
                "tester2@gmail.com", "010-2222-2222", LocalDate.parse("1998-07-02"));

        User user3 = createUser("tester3", "테스터3", "@tester3333",
                "tester3@gmail.com", "010-3333-3333", LocalDate.parse("2020-11-18"));

        // 게시글과 레시피 단계 생성
        createBoardWithSteps("레시피1", "간단한 레시피1입니다.", Category.CHINESE, Method.SOUP,
                "재료1, 재료2, 재료3", user1, List.of("1.jpg", "2.jpg", "3.jpg"),
                20, LocalDateTime.of(2024, 9, 15, 12, 0));

        createBoardWithSteps("레시피2", "간단한 레시피2입니다.", Category.KOREAN, Method.STIR_FRY,
                "재료A, 재료B, 재료C", user2, List.of("2.jpg", "3.jpg", "1.jpg"),
                50, LocalDateTime.of(2024, 11, 11, 13, 0));

        createBoardWithSteps("레시피3", "간단한 레시피3입니다.", Category.FUSION, Method.SALAD,
                "재료X, 재료Y, 재료Z", user3, List.of("3.jpg", "1.jpg", "2.jpg"),
                30, LocalDateTime.of(2024, 11, 24, 14, 0));
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

    private void createBoardWithSteps(String title, String content, Category category, Method method,
                                      String ingredient, User user, List<String> imageFileNames, int likeCount,
                                      LocalDateTime createdDate) {

        // 1. BoardSaveDto 생성
        InitBoardSaveDto boardDto = InitBoardSaveDto.builder()
                .title(title)
                .content(content)
                .category(category)
                .method(method)
                .ingredient(ingredient)
                .nickname(user.getNickname())
                .userId(user.getId())
                .likeCount(likeCount)
                .build();

        // 2. RecipeStepDto 리스트 생성
        List<RecipeStepDto> steps = new ArrayList<>();
        for (int i = 0; i < imageFileNames.size(); i++) {
            String imagePath = "C:/Users/user/.gradle/cookingrecipe/src/main/resources/static/images/" + imageFileNames.get(i);

            // 파일 경로 검증
            Path filePath = Path.of(imagePath);
            if (!Files.exists(filePath)) {
                throw new IllegalArgumentException("파일이 존재하지 않습니다: " + filePath);
            }

            try {
                // 파일 읽기
                byte[] fileBytes = Files.readAllBytes(filePath);

                // MockMultipartFile 생성
                MultipartFile image = new MockMultipartFile(
                        imageFileNames.get(i),  // 파일 이름
                        imageFileNames.get(i),  // 원본 파일 이름
                        "image/jpeg",           // MIME 타입 (필요 시 변경)
                        fileBytes               // 파일 데이터
                );

                // RecipeStepDto 생성
                RecipeStepDto stepDto = RecipeStepDto.builder()
                        .stepOrder(i + 1)
                        .description("단계 " + (i + 1) + " 설명입니다.")
                        .image(image)
                        .build();

                steps.add(stepDto);
            } catch (IOException e) {
                throw new IllegalStateException("이미지 파일 로드 중 문제가 발생했습니다: " + imagePath, e);
            }
        }

        // 3. Board, RecipeStep 저장
        boardService.saveForInitData(boardDto, steps, user, createdDate);
    }


}
