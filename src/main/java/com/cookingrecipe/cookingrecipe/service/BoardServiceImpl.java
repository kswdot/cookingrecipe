package com.cookingrecipe.cookingrecipe.service;

import com.cookingrecipe.cookingrecipe.config.FileService;
import com.cookingrecipe.cookingrecipe.domain.*;
import com.cookingrecipe.cookingrecipe.dto.BoardSaveDto;
import com.cookingrecipe.cookingrecipe.dto.BoardUpdateDto;
import com.cookingrecipe.cookingrecipe.dto.BoardWithImageDto;
import com.cookingrecipe.cookingrecipe.dto.RecipeStepDto;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
import com.cookingrecipe.cookingrecipe.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardRepositoryCustom boardRepositoryCustom;
    private final UserRepository userRepository;
    private final RecipeStepService recipeStepService;
    private final BookmarkRepository bookmarkRepository;
    private final LikeRepository likeRepository;
    private final RecipeStepRepository recipeStepRepository;


    // Board Entity 생성
    @Override
    public Board joinEntity(Board board) {
        return boardRepository.save(board);
    }

    // 게시글 저장
    @Override
    public Long save(BoardSaveDto boardSaveDto, List<RecipeStepDto> recipeStepDto, CustomUserDetails userDetails) {
        FileService.createUploadDir();

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

        try {
            // RecipeStep 저장
            recipeStepService.save(board, recipeStepDto);
        } catch (IOException e) {
            throw new IllegalStateException("레시피 단계 저장 중 문제가 발생했습니다.", e);
        }

        return board.getId();
    }

    @Override
    public Long update(Long boardId, BoardUpdateDto boardUpdateDto, List<RecipeStepDto> steps) {
        FileService.createUploadDir();

        // 게시글 가져오기
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BadRequestException("해당 게시글을 찾을 수 없습니다."));

        // 게시글 업데이트
        board.update(boardUpdateDto.getTitle(), boardUpdateDto.getCategory(),
                boardUpdateDto.getMethod(), boardUpdateDto.getIngredient(),
                boardUpdateDto.getContent());

        // 기존 RecipeStep 제거 후 새로 저장
        recipeStepService.deleteByBoardId(boardId);

        try {
            recipeStepService.save(board, steps);
        } catch (IOException e) {
            throw new IllegalStateException("레시피 단계 저장 중 문제가 발생했습니다.", e);
        }

        return board.getId();
    }

    
    // 게시글 대표 사진 설정 - 레시피 마지막 이미지
    @Override
    public List<BoardWithImageDto> findAllBoardsWithLastImage() {
        List<Board> boards = boardRepository.findAll();
        return boards.stream()
                .map(board -> {
                    String lastImagePath = recipeStepRepository.findLastImagePathByBoardId(board.getId());
                    return new BoardWithImageDto(board, lastImagePath);
                })
                .collect(Collectors.toList());

    }



    // 게시글 검색 - 시스템 ID
    @Override
    public Board findById(Long boardId) {

        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BadRequestException("해당 게시글을 찾을 수 없습니다"));
    }

    @Override
    public Board findBoardWithRecipeSteps(Long boardId) {

        return boardRepository.findWithRecipeStepsById(boardId)
                .orElseThrow(() -> new BadRequestException("해당 게시글을 찾을 수 없습니다"));
    }


    // 게시글 검색 - 검색 조건 - 최신순
    @Override
    public List<Board> searchBoards(String keyword, String ingredient, String nickname) {
        return boardRepositoryCustom.searchBoards(keyword, ingredient, nickname);
    }


    // 게시글 검색 - 검색 조건 - 좋아요 순
    @Override
    public List<Board> searchBoardsOrderByLikes(String keyword, String material, String writer) {
        return boardRepositoryCustom.searchBoardsOrderByLikes(keyword, material, writer);
    }


    // 게시글 검색 - 카테고리 - 최신순
    @Override
    public List<Board> findByCategory(Category category) {
        return boardRepositoryCustom.findByCategory(category);
    }


    // 게시글 검색 - 카테고리 - 좋아요 순
    @Override
    public List<Board> findByCategoryOrderByLikes(Category category) {
        return boardRepositoryCustom.findByCategoryOrderByLikes(category);
    }


    // 게시글 검색 - 카테고리 - 최신순
    @Override
    public List<Board> findByMethod(Method method) {
        return boardRepositoryCustom.findByMethod(method);
    }


    // 게시글 검색 - 카테고리 - 좋아요 순
    @Override
    public List<Board> findByMethodOrderByLikes(Method method) {
        return boardRepositoryCustom.findByMethodOrderByLikes(method);
    }


    // 인기 레시피 TOP10
    @Override
    public List<Board> findTopRecipesByLikes(int limit) {
        return boardRepositoryCustom.findTopRecipesByLikes(limit);
    }


    // 이 달(한 달)의 레시피 TOP10
    @Override
    public List<Board> findMonthlyRecipesByLikes(int limit) {
        return boardRepositoryCustom.findMonthlyRecipesByLikes(limit);
    }


    // 모든 게시글 검색
    @Override
    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    
    // 모든 게시글 검색 - 최신순
    @Override
    public List<Board> findAllByDateDesc() {
        return boardRepositoryCustom.findAllByDateDesc();
    }


    // 게시글 검색 - 유저
    @Override
    public Optional<Board> findByUser(Long boardId) {
        return boardRepository.findByIdWithUser(boardId);
    }


    // 조회수 증가
    @Override
    public void addViewCount(Long boardId) {
        boardRepository.updateViewCount(boardId);
    }


    // 게시글 삭제
    @Override
    public void deleteById(Long boardId) {
        boardRepository.deleteById(boardId);
    }


    // 좋아요 추가
    @Override
    public void addLike(Long boardId) {
        boardRepository.incrementLikeCount(boardId);
    }


    // 좋아요 삭제
    @Override
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


    // InitData 삽입 위한 메서드 생성
    public void saveForInitData(BoardSaveDto boardSaveDto, List<RecipeStepDto> recipeStepDto, User user) {
        FileService.createUploadDir();

        // Board 생성 및 저장
        Board board = Board.builder()
                .title(boardSaveDto.getTitle())
                .content(boardSaveDto.getContent())
                .category(boardSaveDto.getCategory())
                .method(boardSaveDto.getMethod())
                .ingredient(boardSaveDto.getIngredient())
                .nickname(user.getNickname()) // User 엔티티에서 닉네임 가져옴
                .user(user)                   // User 엔티티 저장
                .build();

        boardRepository.save(board);

        try {
            // RecipeStep 저장
            recipeStepService.save(board, recipeStepDto);
        } catch (IOException e) {
            throw new IllegalStateException("레시피 단계 저장 중 문제가 발생했습니다.", e);
        }

    }


}
