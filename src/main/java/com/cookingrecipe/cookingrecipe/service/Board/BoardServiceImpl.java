package com.cookingrecipe.cookingrecipe.service.Board;

import com.cookingrecipe.cookingrecipe.config.FileConfig;
import com.cookingrecipe.cookingrecipe.domain.Board.*;
import com.cookingrecipe.cookingrecipe.domain.User.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.domain.User.User;
import com.cookingrecipe.cookingrecipe.dto.*;
import com.cookingrecipe.cookingrecipe.dto.Board.BoardDto;
import com.cookingrecipe.cookingrecipe.dto.Board.BoardSaveDto;
import com.cookingrecipe.cookingrecipe.dto.Board.BoardUpdateDto;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
import com.cookingrecipe.cookingrecipe.exception.UserNotFoundException;
import com.cookingrecipe.cookingrecipe.repository.*;
import com.cookingrecipe.cookingrecipe.repository.Board.BoardRepository;
import com.cookingrecipe.cookingrecipe.repository.Board.BoardRepositoryCustom;
import com.cookingrecipe.cookingrecipe.service.RecipeStep.RecipeStepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardRepositoryCustom boardRepositoryCustom;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final LikeRepository likeRepository;
    private final BoardMapper boardMapper;
    private final RecipeStepService recipeStepService;


    // 게시글 저장
    @Override
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


        // RecipeStep 저장
        if (recipeStepDto != null && !recipeStepDto.isEmpty()) {
            recipeStepService.saveRecipeSteps(board, recipeStepDto);
        }

        return board.getId();
    }

    @Override
    @Transactional
    public Long update(Long boardId, BoardUpdateDto boardUpdateDto, List<RecipeStepDto> steps) throws IOException {
        // 1. 업로드 경로 생성 및 반환
        String uploadDir = FileConfig.createUploadDir();

        // 2. 게시글 가져오기
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BadRequestException("해당 게시글을 찾을 수 없습니다."));

        // 3. 게시글 업데이트
        board.update(boardUpdateDto.getTitle(), boardUpdateDto.getCategory(),
                boardUpdateDto.getMethod(), boardUpdateDto.getIngredient(),
                boardUpdateDto.getContent());

        // 4. 기존 레시피 호출
        List<RecipeStep> existingSteps = recipeStepService.findByBoardId(boardId);

        // 5. 요청 데이터와 기존 데이터를 비교하여 업데이트
        recipeStepService.updateRecipeSteps(board, existingSteps, steps, uploadDir);

        return board.getId();
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
    @Cacheable(value = "searchResults", key = "#searchCriteria + ':' + #keyword + ':' + #pageable.pageNumber")
    public Page<BoardDto> searchBoards(String searchCriteria, String keyword, Pageable pageable) {

        log.info("Search Criteria: {}", searchCriteria);
        log.info("Keyword: {}", keyword);

        Page<Board> boards = boardRepositoryCustom.searchBoards(searchCriteria, keyword, pageable);

        List<BoardDto> dtoList = boardMapper.mapToBoardDto(boards.getContent());

        return new PageImpl<>(dtoList, pageable, boards.getTotalElements());
    }


    // 게시글 검색 - 검색 조건 : 좋아요 순
    @Override
    @Cacheable(value = "searchResultsByLikes", key = "#searchCriteria + ':' + #keyword + ':' + #pageable.pageNumber")
    public Page<BoardDto> searchBoardsOrderByLikes(String searchCriteria, String keyword, Pageable pageable) {
        Page<Board> boards = boardRepositoryCustom.searchBoardsOrderByLikes(searchCriteria, keyword, pageable);

        List<BoardDto> dtoList = boardMapper.mapToBoardDto(boards.getContent());

        return new PageImpl<>(dtoList, pageable, boards.getTotalElements());
    }


    // 게시글 검색 - 카테고리 - 최신순
    @Override
    public List<BoardDto> findByCategory(Category category) {
        List<Board> boards = boardRepositoryCustom.findByCategory(category);

        return boardMapper.mapToBoardDto(boards);
    }


    // 게시글 검색 - 카테고리 - 좋아요 순
    @Override
    public List<BoardDto> findByCategoryOrderByLikes(Category category) {
        List<Board> boards = boardRepositoryCustom.findByCategoryOrderByLikes(category);

        return boardMapper.mapToBoardDto(boards);
    }


    // 게시글 검색 - 카테고리 - 최신순
    @Override
    public List<BoardDto> findByMethod(Method method) {
        List<Board> boards = boardRepositoryCustom.findByMethod(method);

        return boardMapper.mapToBoardDto(boards);
    }


    // 게시글 검색 - 카테고리 - 좋아요 순
    @Override
    public List<BoardDto> findByMethodOrderByLikes(Method method) {
        List<Board> boards = boardRepositoryCustom.findByMethodOrderByLikes(method);

        return boardMapper.mapToBoardDto(boards);
    }


    // 인기 레시피 TOP10
    @Override
    public List<BoardDto> findTopRecipesByLikes(int limit) {
        List<Board> boards = boardRepositoryCustom.findTopRecipesByLikes(limit);

        return boardMapper.mapToBoardDto(boards);
    }


    // 이 달(한 달)의 레시피 TOP10
    @Override
    public List<BoardDto> findMonthlyRecipesByLikes(int limit) {
        List<Board> boards = boardRepositoryCustom.findMonthlyRecipesByLikes(limit);

        return boardMapper.mapToBoardDto(boards);
    }


    // 모든 게시글 검색
    @Override
    public List<BoardDto> findAll() {
        List<Board> boards = boardRepository.findAll();

        return boardMapper.mapToBoardDto(boards);
    }


    // 모든 게시글 검색 - 최신순
    @Override
    public Page<BoardDto> findAllByDateDesc(Pageable pageable) {
        Page<Board> boardPage = boardRepositoryCustom.findAllByDateDesc(pageable);

        List<BoardDto> boardDtos = boardMapper.mapToBoardDto(boardPage.getContent());

        // Page 객체로 변환하여 반환
        return new PageImpl<>(boardDtos, pageable, boardPage.getTotalElements());
    }


    // 게시글 검색 - 유저
    @Override
    public Optional<Board> findByIdWithUser(Long boardId) {
        return boardRepository.findByIdWithUser(boardId);
    }


    // 조회수 증가 - HTTP Session 사용
    @Override
    @Transactional
    public void addViewCount(Long boardId) {
        boardRepository.updateViewCount(boardId);
    }

    
    // 조회수 증가 - Redis 사용
    @Override
    public boolean addViewCountWithRedis(Long boardId, Long userId) {
        return boardRepositoryCustom.addViewCountWithRedis(boardId, userId);
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
        FileConfig.createUploadDir();

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
            recipeStepService.saveRecipeSteps(board, recipeStepDto);
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