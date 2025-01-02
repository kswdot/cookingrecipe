package com.cookingrecipe.cookingrecipe.config;

import com.cookingrecipe.cookingrecipe.domain.*;
import com.cookingrecipe.cookingrecipe.domain.Board.Category;
import com.cookingrecipe.cookingrecipe.domain.Board.Method;
import com.cookingrecipe.cookingrecipe.domain.User.User;
import com.cookingrecipe.cookingrecipe.dto.InitBoardSaveDto;
import com.cookingrecipe.cookingrecipe.dto.RecipeStepDto;
import com.cookingrecipe.cookingrecipe.dto.User.UserSignupDto;
import com.cookingrecipe.cookingrecipe.exception.UserNotFoundException;
import com.cookingrecipe.cookingrecipe.repository.NotificationRepository;
import com.cookingrecipe.cookingrecipe.service.Board.BoardService;
import com.cookingrecipe.cookingrecipe.service.User.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
@Profile("!docker")
public class InitData implements CommandLineRunner {

    private final UserService userService;
    private final BoardService boardService;
    private final NotificationRepository notificationRepository;

    @Value("${file.upload-dir}") // application.yml에서 경로를 읽음
    private String uploadDir;


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        User user1 = createUser("tester1", "테스터1", "@tester1111",
                "tester1@gmail.com", "010-1111-1111", LocalDate.parse("2024-11-11"));

        User user2 = createUser("tester2", "테스터2", "@tester2222",
                "tester2@gmail.com", "010-2222-2222", LocalDate.parse("1998-07-02"));

        User user3 = createUser("tester3", "테스터3", "@tester3333",
                "tester3@gmail.com", "010-3333-3333", LocalDate.parse("2020-11-18"));

        User admin1 = createAdmin("admin1", "관리자1", "@admin1111",
                "admin1@gmail.com", "010-9999-9999", LocalDate.parse("2020-09-09"));

        // 게시글과 레시피 단계 생성
        createBoardWithSteps("들깨 미역국", "고소한 들깨 미역국 레시피", Category.KOREAN, Method.SOUP,
                "미역, 들깨가루, 다진 마늘, 들기름", user1, List.of("미역국1.jpg", "미역국2.jpg", "미역국3.jpg"),
                20, LocalDateTime.of(2024, 9, 15, 12, 0));

        createBoardWithSteps("고구마 라자냐", "크리스마스 기념 고구마 라자냐", Category.WESTERN, Method.STEAM,
                "삶은 고구마, 모짜렐라 치즈, 스파게티 소스, 핫소스", user2, List.of("라자냐1.jpg", "라자냐2.jpg", "라자냐3.jpg"),
                50, LocalDateTime.of(2024, 12, 11, 13, 0));

        createBoardWithSteps("소고기 무국", "추운 날씨에 어울리는 소고기 무국", Category.KOREAN, Method.SOUP,
                "소고기, 다진 마늘, 무, 국간장", user3, List.of("무국1.jpg", "무국2.jpg", "무국3.jpg"),
                5, LocalDateTime.of(2024, 11, 24, 14, 0));

        createBoardWithSteps("두부 샐러드", "담백하고 고소한 이색 메뉴", Category.KOREAN, Method.SALAD,
                "두부, 올리브유, 어린잎채소, 방울토마토", user1, List.of("두부1.jpg", "두부2.jpg", "두부3.jpg", "두부4.jpg"),
                11, LocalDateTime.of(2024, 3, 3, 3, 0));

        createBoardWithSteps("명란버터우동", "국물 없이 간단하게 비벼먹는 우동", Category.JAPANESE, Method.MIX,
                "우동면, 쪽파, 달걀, 쯔유, 명란젓, 버터", user1, List.of("명란1.jpg", "명란2.jpg", "명란3.jpg"),
                0, LocalDateTime.of(2024, 12, 1, 14, 0));

        createBoardWithSteps("어니언링", "양파 하나만 있으면 바로 집에서 요리가능!", Category.ETC, Method.DEEP_FRY,
                "양파, 튀김가루, 달걀, 빵가루, 식용유", user2, List.of("양파1.jpg", "양파2.jpg", "양파3.jpg"),
                100, LocalDateTime.of(2023, 11, 2, 1, 0));

        createBoardWithSteps("순두부국", "양념장 간단하게 만들어서 같이 먹는 순두부국", Category.KOREAN, Method.SOUP,
                "멸치육수, 순두부, 달걀, 새우젓, 청양고추", user3, List.of("순두부1.jpg", "순두부2.jpg", "순두부3.jpg"),
                7, LocalDateTime.of(2024, 1, 5, 5, 0));

        createBoardWithSteps("순두부국", "양념장 간단하게 만들어서 같이 먹는 순두부국", Category.KOREAN, Method.SOUP,
                "멸치육수, 순두부, 달걀, 새우젓, 청양고추", user3, List.of("무국1.jpg", "무국2.jpg", "무국3.jpg"),
                100, LocalDateTime.of(2024, 1, 5, 5, 0));

        createBoardWithSteps("예시1", "양념장 간단하게 만들어서 같이 먹는 순두부국", Category.KOREAN, Method.MIX,
                "멸치육수, 순두부, 달걀, 새우젓, 청양고추", user3, List.of("명란1.jpg", "명란2.jpg", "명란3.jpg"),
                44, LocalDateTime.of(2024, 12, 5, 5, 0));

        createBoardWithSteps("예시2", "양념장 간단하게 만들어서 같이 먹는 순두부국", Category.DESSERT, Method.STEAM,
                "멸치육수, 순두부, 달걀, 새우젓, 청양고추", user2, List.of("양파1.jpg", "양파2.jpg", "양파3.jpg"),
                35, LocalDateTime.of(2023, 1, 5, 5, 0));

        createBoardWithSteps("예시3", "양념장 간단하게 만들어서 같이 먹는 순두부국", Category.FUSION, Method.ETC,
                "멸치육수, 순두부, 달걀, 새우젓, 청양고추", user1, List.of("순두부1.jpg", "순두부2.jpg", "순두부3.jpg"),
                65, LocalDateTime.of(2020, 5, 5, 5, 0));

        createBoardWithSteps("예시4", "양념장 간단하게 만들어서 같이 먹는 순두부국", Category.ETC, Method.MIX,
                "멸치육수, 순두부, 달걀, 새우젓, 청양고추", user3, List.of("라자냐3.jpg"),
                28, LocalDateTime.of(2023, 1, 5, 5, 0));

        createBoardWithSteps("예시5", "양념장 간단하게 만들어서 같이 먹는 순두부국", Category.SOUTHEAST, Method.RAW,
                "멸치육수, 순두부, 달걀, 새우젓, 청양고추", user1, List.of("양파3.jpg"),
                72, LocalDateTime.of(2024, 12, 5, 5, 0));

        createBoardWithSteps("예시5", "양념장 간단하게 만들어서 같이 먹는 순두부국", Category.DESSERT, Method.RAW,
                "멸치육수, 순두부, 달걀, 새우젓, 청양고추", user1, List.of("미역국3.jpg"),
                72, LocalDateTime.of(2023, 7, 2, 5, 0));


        // 테스트용 알림 생성
        createNotification(user1, null, NotificationType.LIKE, 1L, null, null, false, LocalDateTime.now());
        createNotification(user2, null, NotificationType.COMMENT, 1L, 101L, "테스트 댓글 내용", false, LocalDateTime.now());
        createNotification(user3, null, NotificationType.COMMENT, 2L, 102L, "다른 댓글 내용", true, LocalDateTime.now());
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


    private User createAdmin(String loginId, String nickname, String password, String email, String number, LocalDate birth) {
        if (!userService.isLoginIdDuplicated(loginId) && !userService.isEmailDuplicated(email)) {
            UserSignupDto userSignupDto = UserSignupDto.builder()
                    .loginId(loginId)
                    .nickname(nickname)
                    .password(password)
                    .email(email)
                    .number(number)
                    .birth(birth)
                    .build();

            userService.joinAdmin(userSignupDto);
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
            String imagePath = uploadDir + "/" + imageFileNames.get(i); // 업로드 경로로 변경

            // 파일 경로 검증
            Path filePath = Path.of(uploadDir, imageFileNames.get(i));
            log.info("Checking file path: {}", filePath); // 디버깅 로그 추가

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
                        "image/jpeg",           // MIME 타입
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


    private void createNotification(User user, User sender, NotificationType type,
                                    Long boardId, Long commentId, String commentContent,
                                    boolean isRead, LocalDateTime createdAt) {
        Notification notification = new Notification(
                null, // ID 자동 생성
                user.getId(),
                type,
                boardId,
                commentId,
                commentContent,
                isRead,
                createdAt
        );

        notificationRepository.save(notification);
    }
}
