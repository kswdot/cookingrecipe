package com.cookingrecipe.cookingrecipe.service;

import com.cookingrecipe.cookingrecipe.domain.*;
import com.cookingrecipe.cookingrecipe.dto.BoardSaveDto;
import com.cookingrecipe.cookingrecipe.dto.BoardUpdateDto;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
import com.cookingrecipe.cookingrecipe.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardRepositoryCustom boardRepositoryCustom;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final BookmarkRepository bookmarkRepository;


    // 게시글 저장
    @Override
    public Long save(BoardSaveDto boardSaveDto, List<MultipartFile> images, CustomUserDetails userDetails) {
        String uploadDir = System.getProperty("user.dir") + "/uploaded-images/";

        try {
            // Board 생성
            Board board = Board.builder()
                    .title(boardSaveDto.getTitle())
                    .content(boardSaveDto.getContent())
                    .category(boardSaveDto.getCategory())
                    .method(boardSaveDto.getMethod())
                    .ingredient(boardSaveDto.getIngredient())
                    .nickname(userDetails.getNickname())
                    .user(userDetails.getUser())
                    .build();

            // 이미지 추가
            for (MultipartFile image : images) {
                String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + fileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(image.getInputStream(), filePath);

                // 이미지 엔티티 생성 및 Board에 추가
                Image savedImage = new Image(fileName, filePath.toString(), board);
                board.getImages().add(savedImage); // Board와 연관
            }

            // Board 저장 (CascadeType.ALL로 인해 이미지도 저장됨)
            boardRepository.save(board);

            return board.getId();
        } catch (Exception e) {
            throw new IllegalStateException("파일 업로드 중 문제가 발생했습니다.", e);
        }
    }



    // 게시글 수정
    @Override
    public Long update(Long boardId, BoardUpdateDto boardUpdateDto) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BadRequestException("해당 게시글을 찾을 수 없습니다."));

        board.update(boardUpdateDto.getTitle(), boardUpdateDto.getCategory(),
                boardUpdateDto.getMethod(), boardUpdateDto.getIngredient(),
                boardUpdateDto.getContent());

        imageRepository.deleteByBoardId(boardId);

        List<Image> images = boardUpdateDto.getImages().stream()
                .map(imageDto -> new Image(imageDto.getName(), imageDto.getPath(), board))
                .collect(Collectors.toList());
        imageRepository.saveAll(images);

        return board.getId();
    }


    // 게시글 검색 - 시스템 ID
    @Override
    public Board findById(Long boardId) {

        return boardRepository.findById(boardId)
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
        Board board = boardRepository.findById(boardId)

                .orElseThrow(() -> new BadRequestException("해당 게시글을 찾을 수 없습니다"));

        board.incrementView();
        boardRepository.save(board); // 명시적으로 저장
    }


    // 게시글 삭제
    @Override
    public void deleteById(Long boardId) {
        boardRepository.deleteById(boardId);
    }


    // 좋아요 추가
    @Override
    public void addLike(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BadRequestException("해당 게시글을 찾을 수 없습니다"));

        board.incrementLikeCount();
        boardRepository.save(board); // 명시적으로 저장
    }


    // 좋아요 삭제
    @Override
    public void removeLike(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BadRequestException("해당 게시글을 찾을 수 없습니다"));

        board.decrementLikeCount();
        boardRepository.save(board); // 명시적으로 저장
    }


    // 북마크 추가/삭제 토글
    @Override
    public boolean toggleBookmark(Long boardId, Long userId) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BadRequestException("해당 게시글을 찾을 수 없습니다"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("해당 사용자를 찾을 수 없습니다"));

        // 북마크 여부 확인
        Optional<Bookmark> checkBookmark = bookmarkRepository.findByUserAndBoard(user, board);

        // 북마크 존재 -> 삭제
        if (checkBookmark.isPresent()) {
            bookmarkRepository.delete(checkBookmark.get());
            board.decrementBookmarkCount();
            return false; // 북마크가 해제되었음을 의미
        } else {
            // 북마크가 존재하지 않을 때 -> 추가
            Bookmark newBookmark = new Bookmark(user, board);
            bookmarkRepository.save(newBookmark);
            board.incrementBookmarkCount();
            return true; // 북마크가 추가되었음을 의미
        }
    }

}
