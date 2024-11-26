package com.cookingrecipe.cookingrecipe.service;

import com.cookingrecipe.cookingrecipe.config.FileService;
import com.cookingrecipe.cookingrecipe.domain.*;
import com.cookingrecipe.cookingrecipe.dto.*;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
import com.cookingrecipe.cookingrecipe.exception.UserNotFoundException;
import com.cookingrecipe.cookingrecipe.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardRepositoryCustom boardRepositoryCustom;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final LikeRepository likeRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final BoardMapper boardMapper;


    // Board Entity 생성
    @Override
    @Transactional
    public Board joinEntity(Board board) {
        return boardRepository.save(board);
    }


    // 게시글 저장
    @Override
    @Transactional
    public Long save(BoardSaveDto boardSaveDto, List<RecipeStepDto> recipeStepDto, CustomUserDetails userDetails) throws IOException {
        // 사용자 정보 확인
        if (userDetails == null || userDetails.getUser() == null) {
            throw new UserNotFoundException("해당 사용자를 찾을 수 없습니다");
        }

        // Board 생성 및 저장
        Board board = Board.builder()
                .title(boardSaveDto.getTitle())
                .content(boardSaveDto.getContent())
                .category(boardSaveDto.getCategory())
                .method(boardSaveDto.getMethod())
                .ingredient(boardSaveDto.getIngredient())
                .nickname(userDetails.getNickname())
                .user(userDetails.getUser())
                .build();

        boardRepository.save(board);
        // 로그 추가: 전달된 레시피 단계 확인

        log.info("Recipe steps before processing: {}", recipeStepDto);


        // RecipeStep 저장
        if (recipeStepDto != null && !recipeStepDto.isEmpty()) {
            saveRecipeSteps(board, recipeStepDto);
        }

        return board.getId();
    }


    // 게시글 저장 - 레시피 생성 및 저장
    private void saveRecipeSteps(Board board, List<RecipeStepDto> recipeStepDto) throws IOException {
        String uploadDir = System.getProperty("user.dir") + "/uploaded-images";

        int stepOrder = 1;

        for (RecipeStepDto stepDto : recipeStepDto) {
            if (stepDto.getDescription() == null || stepDto.getDescription().isBlank()) {
                continue;
            }

            if (stepDto.getImage() == null || stepDto.getImage().isEmpty()) {
                String errorMessage = "레시피 단계 " + stepOrder + "에 이미지가 필요합니다";
                log.error("Validation error: {}", errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            String fileName = saveFile(stepDto.getImage(), uploadDir);

            // RecipeStep 생성 및 저장
            RecipeStep recipeStep = RecipeStep.builder()
                    .stepOrder(stepOrder++) // 단계 번호 자동 증가
                    .description(stepDto.getDescription())
                    .imagePath(fileName)
                    .board(board)
                    .build();
            recipeStepRepository.save(recipeStep);
        }
    }




    // 이미지 파일 저장 및 처리
    private String saveFile(MultipartFile file, String uploadDir) throws IOException {
        if (file == null || file.isEmpty()) {
            log.error("File is empty or null: {}", file);
            throw new IllegalArgumentException("파일이 비어있거나 null입니다.");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            log.error("Invalid file name: {}", originalFileName);
            throw new IllegalArgumentException("파일 이름이 유효하지 않습니다.");
        }

        String fileExtension = originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf("."))
                : ".jpg";

        String fileName = UUID.randomUUID() + fileExtension;
        Path filePath = Paths.get(uploadDir, fileName);

        try {
            Files.createDirectories(filePath.getParent());
            Files.copy(file.getInputStream(), filePath);
            log.info("File saved successfully: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to save file: {}", filePath, e);
            throw new IOException("파일 저장 중 문제가 발생했습니다.", e);
        }

        return "/uploads/" + fileName;
    }


    // 게시글 수정
    @Override
    @Transactional
    public Long update(Long boardId, BoardUpdateDto boardUpdateDto, List<RecipeStepDto> steps) throws IOException {
        FileService.createUploadDir();

        // 1. 게시글 가져오기
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BadRequestException("해당 게시글을 찾을 수 없습니다."));

        // 2. 게시글 업데이트
        board.update(boardUpdateDto.getTitle(), boardUpdateDto.getCategory(),
                boardUpdateDto.getMethod(), boardUpdateDto.getIngredient(),
                boardUpdateDto.getContent());

        // 3. 기존 레시피 호출
        List<RecipeStep> existingSteps = recipeStepRepository.findByBoardId(boardId);

        // 4. 요청 데이터와 기존 데이터를 비교하여 업데이트
        updateRecipeSteps(board, existingSteps, steps);

        return board.getId();
    }


    private void updateRecipeSteps(Board board, List<RecipeStep> existingSteps, List<RecipeStepDto> steps) throws IOException {
        String uploadDir = System.getProperty("user.dir") + "/uploaded-images";

        // 요청 데이터 기준으로 매핑 (stepOrder 키로 사용)
        Map<Integer, RecipeStepDto> stepDtoMap = steps.stream()
                .collect(Collectors.toMap(RecipeStepDto::getStepOrder, stepDto -> stepDto));

        // 1. 기존 단계 처리
        for (RecipeStep existingStep : existingSteps) {
            RecipeStepDto stepDto = stepDtoMap.remove(existingStep.getStepOrder());

            if (stepDto != null) {
                // 내용이 변경된 경우 업데이트
                updateExistingStep(existingStep, stepDto, uploadDir);
            } else {
                // 요청에 없는 단계는 삭제
                recipeStepRepository.delete(existingStep);
            }
        }

        // 2. 요청에 있지만 기존에 없는 단계는 새로 추가
        for (RecipeStepDto stepDto : stepDtoMap.values()) {
            saveNewStep(board, stepDto, uploadDir);
        }
    }


    private void updateExistingStep(RecipeStep existingStep, RecipeStepDto stepDto, String uploadDir) throws IOException {
        // 파일 저장 로직을 saveFile 메서드로 대체
        String fileName = saveFile(stepDto.getImage(), uploadDir);

        // 단계 내용 업데이트
        existingStep.update(stepDto.getDescription(), fileName);
    }


    private void saveNewStep(Board board, RecipeStepDto stepDto, String uploadDir) throws IOException {
        // 파일 저장 로직을 saveFile 메서드로 대체
        String fileName = saveFile(stepDto.getImage(), uploadDir);

        // 새로운 RecipeStep 생성 및 저장
        RecipeStep recipeStep = RecipeStep.builder()
                .stepOrder(stepDto.getStepOrder())
                .description(stepDto.getDescription())
                .imagePath(fileName) // 저장된 파일 경로 설정
                .board(board)
                .build();

        recipeStepRepository.save(recipeStep);
    }


    // 게시글 검색 - 시스템 ID
    @Override
    public Board findById(Long boardId) {

        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BadRequestException("해당 게시글을 찾을 수 없습니다"));
    }


    // 특정 게시글과 속한 레시피 검색
    @Override
    public Board findBoardWithRecipeSteps(Long boardId) {

        return boardRepository.findWithRecipeStepsById(boardId)
                .orElseThrow(() -> new BadRequestException("해당 게시글을 찾을 수 없습니다"));
    }


    // 게시글 검색 - 검색 조건 : 최신순
    @Override
    public List<BoardWithImageDto> searchBoards(String searchCriteria, String keyword) {
        List<Board> boards = boardRepositoryCustom.searchBoards(searchCriteria, keyword);

        return boardMapper.findBoardsWithMainImages(boards);
    }


    // 게시글 검색 - 검색 조건 : 좋아요 순
    @Override
    public List<BoardWithImageDto> searchBoardsOrderByLikes(String searchCriteria, String keyword) {
        List<Board> boards = boardRepositoryCustom.searchBoardsOrderByLikes(searchCriteria, keyword);

        return boardMapper.findBoardsWithMainImages(boards);
    }


    // 게시글 검색 - 카테고리 - 최신순
    @Override
    public List<BoardWithImageDto> findByCategory(Category category) {
        List<Board> boards = boardRepositoryCustom.findByCategory(category);

        return boardMapper.findBoardsWithMainImages(boards);
    }


    // 게시글 검색 - 카테고리 - 좋아요 순
    @Override
    public List<BoardWithImageDto> findByCategoryOrderByLikes(Category category) {
        List<Board> boards = boardRepositoryCustom.findByCategoryOrderByLikes(category);

        return boardMapper.findBoardsWithMainImages(boards);
    }


    // 게시글 검색 - 카테고리 - 최신순
    @Override
    public List<BoardWithImageDto> findByMethod(Method method) {
        List<Board> boards = boardRepositoryCustom.findByMethod(method);

        return boardMapper.findBoardsWithMainImages(boards);
    }


    // 게시글 검색 - 카테고리 - 좋아요 순
    @Override
    public List<BoardWithImageDto> findByMethodOrderByLikes(Method method) {
        List<Board> boards = boardRepositoryCustom.findByMethodOrderByLikes(method);

        return boardMapper.findBoardsWithMainImages(boards);
    }


    // 인기 레시피 TOP10
    @Override
    public List<BoardWithImageDto> findTopRecipesByLikes(int limit) {
        List<Board> boards = boardRepositoryCustom.findTopRecipesByLikes(limit);

        return boardMapper.findBoardsWithMainImages(boards);
    }


    // 이 달(한 달)의 레시피 TOP10
    @Override
    public List<BoardWithImageDto> findMonthlyRecipesByLikes(int limit) {
        List<Board> boards = boardRepositoryCustom.findMonthlyRecipesByLikes(limit);

        return boardMapper.findBoardsWithMainImages(boards);
    }


    // 모든 게시글 검색
    @Override
    public List<BoardWithImageDto> findAll() {
        List<Board> boards = boardRepository.findAll();

        return boardMapper.findBoardsWithMainImages(boards);
    }


    // 모든 게시글 검색 - 최신순
    @Override
    public List<BoardWithImageDto> findAllByDateDesc() {
        List<Board> boards = boardRepositoryCustom.findAllByDateDesc();

        return boardMapper.findBoardsWithMainImages(boards);
    }


    // 게시글 검색 - 유저
    @Override
    public Optional<Board> findByIdWithUser(Long boardId) {
        return boardRepository.findByIdWithUser(boardId);
    }


    // 조회수 증가
    @Override
    @Transactional
    public void addViewCount(Long boardId) {
        boardRepository.updateViewCount(boardId);
    }


    // 게시글 삭제
    @Override
    @Transactional
    public void deleteById(Long boardId) {
        boardRepository.deleteById(boardId);
    }


    // 좋아요 추가
    @Override
    @Transactional
    public void addLike(Long boardId) {
        boardRepository.incrementLikeCount(boardId);
    }


    // 좋아요 삭제
    @Override
    @Transactional
    public void removeLike(Long boardId) {
        boardRepository.decrementLikeCount(boardId);
    }


    // 좋아요 여부 확인
    @Override
    public boolean isLikedByUser(Long boardId, Long userId) {
        return likeRepository.findByBoardIdAndUserId(boardId, userId).isPresent();
    }


    // 북마크 여부 확인
    @Override
    public boolean isBookmarkedByUser(Long boardId, Long userId) {
        return bookmarkRepository.findByBoardIdAndUserId(boardId, userId).isPresent();
    }


    // 좋아요 토글
    @Override
    @Transactional
    public void toggleLike(Long boardId, Long userId) {

        // 좋아요 여부 확인
        Optional<Like> existingLike = likeRepository.findByBoardIdAndUserId(boardId, userId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BadRequestException("게시글을 찾을 수 없습니다"));


        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            board.decrementLikeCount();
        } else {
            Like like = likeRepository.save(new Like(userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException("사용자를 찾을 수 없습니다")), board));
            likeRepository.save(like);
            board.incrementLikeCount();
        }

        boardRepository.save(board);
    }


    // 북마크 토글
    @Override
    @Transactional
    public void toggleBookmark(Long boardId, Long userId) {
        Optional<Bookmark> existingBookmark = bookmarkRepository.findByBoardIdAndUserId(boardId, userId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BadRequestException("게시글을 찾을 수 없습니다"));

        if (existingBookmark.isPresent()) {
            bookmarkRepository.delete(existingBookmark.get());
        } else {
            Bookmark bookmark = bookmarkRepository.save(new Bookmark(userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException("사용자를 찾을 수 없습니다")), board));
            bookmarkRepository.save(bookmark);
        }

        boardRepository.save(board);
    }


    @Transactional
    // InitData 삽입 위한 메서드 생성
    public void saveForInitData(InitBoardSaveDto initBoardSaveDto, List<RecipeStepDto> recipeStepDto, User user, LocalDateTime createdDate) {
        FileService.createUploadDir();

        // Board 생성 및 저장
        Board board = Board.builder()
                .title(initBoardSaveDto.getTitle())
                .content(initBoardSaveDto.getContent())
                .category(initBoardSaveDto.getCategory())
                .method(initBoardSaveDto.getMethod())
                .ingredient(initBoardSaveDto.getIngredient())
                .nickname(user.getNickname()) // User 엔티티에서 닉네임 가져옴
                .user(user)                   // User 엔티티 저장
                .likeCount(initBoardSaveDto.getLikeCount())
                .build();

        // 리플렉션을 이용해 createdDate 강제 설정
        setCreatedDate(board, createdDate);

        boardRepository.save(board);

        try {
            saveRecipeSteps(board, recipeStepDto);
        } catch (IOException e) {
            throw new IllegalStateException("레시피 단계 저장 중 문제가 발생했습니다.", e);
        }

    }

    private void setCreatedDate(Object entity, LocalDateTime createdDate) {
        try {
            // 슈퍼클래스(BaseEntity)의 createdDate 필드를 가져온다.
            Field field = entity.getClass().getSuperclass().getDeclaredField("createdDate");
            field.setAccessible(true); // 필드 접근 허용
            field.set(entity, createdDate); // 필드 값 설정
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Created date 설정 중 문제가 발생했습니다.", e);
        }
    }



}