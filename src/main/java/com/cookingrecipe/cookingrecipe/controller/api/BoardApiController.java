package com.cookingrecipe.cookingrecipe.controller.api;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.domain.Category;
import com.cookingrecipe.cookingrecipe.domain.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.domain.Method;
import com.cookingrecipe.cookingrecipe.dto.*;
import com.cookingrecipe.cookingrecipe.dto.api.BoardResponseDto;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
import com.cookingrecipe.cookingrecipe.service.Board.BoardService;
import com.cookingrecipe.cookingrecipe.service.Comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardApiController {


    private final BoardService boardService;
    private final CommentService commentService;


    // 전체 게시글 조회
    @GetMapping("")
    public ResponseEntity<?> all(@PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<BoardWithImageDto> boardPage = boardService.findAllByDateDesc(pageable);

            List<BoardResponseDto> response = boardPage.getContent().stream()
                    .map(BoardResponseDto::from)
                    .toList();

            Map<String, Object> responseBody = Map.of(
                    "content", response, // 게시글 데이터
                    "currentPage", boardPage.getNumber(),
                    "totalPages", boardPage.getTotalPages(),
                    "totalElements", boardPage.getTotalElements()
            );

            return ResponseEntity.ok().body(responseBody);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "게시글을 조회하던 중 문제가 발생했습니다"));
        }
    }



    // 게시글 작성
    @PostMapping("")
    public ResponseEntity<?> save(@RequestBody @Validated BoardSaveDto boardSaveDto, BindingResult bindingResult,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "해당 사용자를 찾지 못하였습니다. 다시 로그인해주세요"));
        }

        // 유효성 검사
        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> Map.of(
                            "field", error.getField(),
                            "message", error.getDefaultMessage()
                    ))
                    .toList();

            return ResponseEntity.badRequest().body(errorMessages);
        }

        try {
            Long boardId = boardService.save(boardSaveDto, boardSaveDto.getRecipeSteps(), userDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "게시글 작성 완료!",
                    "id", boardId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "게시글 작성 중 오류가 발생했습니다. 다시 시도해주세요."));
        }
    }


    // 특정 게시글 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> view(@PathVariable("id") Long boardId,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails != null ? userDetails.getId() : 0L; // 로그인되지 않은 경우 0L로 처리

        boolean isNewView = boardService.addViewCountWithRedis(boardId, userId);

        // 게시글과 속한 레시피 조회
        Board board = boardService.findByIdWithUser(boardId)
                .orElseThrow(() -> new BadRequestException("게시글을 찾을 수 없습니다"));


        List<CommentResponseDto> comments = commentService.findByBoard(boardId);


        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("board", board);
        response.put("viewCount", board.getView()); // DB 조회
        response.put("likeCount", board.getLikeCount()); // 좋아요 수
        response.put("isNewView", isNewView);
        response.put("comments", comments);


        return ResponseEntity.ok(response);
    }


    // 게시글 수정
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @ModelAttribute("form") BoardUpdateDto boardUpdateDto,
                                    BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다"));
        }


        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> Map.of(
                            "field", error.getField(),
                            "message", error.getDefaultMessage()
                    ))
                    .toList();

            return ResponseEntity.badRequest().body(errorMessages);
        }

        try {
            boardService.update(id, boardUpdateDto, boardUpdateDto.getRecipeSteps());
            return ResponseEntity.ok(Map.of("message", "게시글이 성공적으로 수정되었습니다"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("errorMessage", "게시글 수정 중 오류가 발생했습니다"));
        }
    }


    // 게시글 좋아요 토글
    @PatchMapping("/{id}/like")
    public ResponseEntity<?> like(@PathVariable("id") Long boardId,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다"));
        }

        try {
            boardService.toggleLike(boardId, userDetails.getId());
            return ResponseEntity.ok(Map.of(
                    "message", "좋아요 상태가 변경되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "좋아요 설정 중 오류가 발생했습니다"));
        }
    }


    // 게시글 북마크 토글
    @PatchMapping("/{id}/bookmark")
    public ResponseEntity<?> toggleBookmark(@PathVariable("id") Long boardId,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다"));
        }

        try {
            boardService.toggleBookmark(boardId, userDetails.getId());
            return ResponseEntity.ok(Map.of(
                    "message", "북마크 상태가 변경되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "북마크 설정 중 오류가 발생했습니다"));
        }
    }


    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long boardId,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다"));
        }

        try {
            // 게시글 작성자 확인
            Board board = boardService.findByIdWithUser(boardId)
                    .orElseThrow(() -> new BadRequestException("게시글을 찾을 수 없습니다"));

            if (!board.getUser().getId().equals(userDetails.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "게시글 삭제 권한이 없습니다"));
            }

            // 삭제 로직 실행
            boardService.deleteById(boardId);

            return ResponseEntity.ok(Map.of("message", "게시글이 성공적으로 삭제되었습니다."));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "게시글 삭제 중 오류가 발생했습니다."));
        }
    }


    // 게시글 검색 : 검색 조건
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(required = false) String searchCriteria,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(defaultValue = "date") String sort,
                                    @RequestParam(defaultValue = "0") int page,   // 현재 페이지 번호 (0부터 시작)
                                    @RequestParam(defaultValue = "9") int size) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errorMessage", "검색어를 입력하세요"));
        }


        try {
            // Pageable 객체 생성
            Pageable pageable = PageRequest.of(page, size, Sort.by("date".equals(sort) ? "createdDate" : "likeCount").descending());

            // 페이징된 검색 결과 가져오기
            Page<BoardWithImageDto> boards = "likes".equals(sort) ?
                    boardService.searchBoardsOrderByLikes(searchCriteria, keyword, pageable) :
                    boardService.searchBoards(searchCriteria, keyword, pageable);

            // 응답 DTO로 변환
            List<BoardResponseDto> response = boards.getContent().stream()
                    .map(BoardResponseDto::from)
                    .toList();

            // 페이징 메타데이터 포함 반환
            return ResponseEntity.ok(Map.of(
                    "content", response,
                    "currentPage", boards.getNumber(),
                    "totalPages", boards.getTotalPages(),
                    "totalElements", boards.getTotalElements()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("errorMessage", "검색 중 문제가 발생하였습니다. 다시 시도해주세요."));
        }
    }


    // 전체 레시피 TOP 10
    @GetMapping("/top")
    public ResponseEntity<?> top() {

        try {
            List<BoardWithImageDto> boards = boardService.findTopRecipesByLikes(10);

            List<BoardResponseDto> response = boards.stream()
                    .map(BoardResponseDto::from)
                    .toList();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("errorMessage", "검색 중 문제가 발생하였습니다. 다시 시도해주세요."));
        }
    }


    // 이달의 레시피 TOP 10
    @GetMapping("/monthly")
    public ResponseEntity<?> monthly() {

        try {
            List<BoardWithImageDto> boards = boardService.findMonthlyRecipesByLikes(10);

            List<BoardResponseDto> response = boards.stream()
                    .map(BoardResponseDto::from)
                    .toList();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("errorMessage", "검색 중 문제가 발생하였습니다. 다시 시도해주세요."));
        }
    }


    // 카테고리 검색
    @GetMapping("/category/{category}")
    public ResponseEntity<?> category(@PathVariable Category category,
                                      @RequestParam(defaultValue = "date") String sort) {

        try {
            List<BoardWithImageDto> boards;

            if ("likes".equals(sort)) {
                boards = boardService.findByCategoryOrderByLikes(category);
            } else {
                boards = boardService.findByCategory(category);
            }

            List<BoardResponseDto> response = boards.stream()
                    .map(BoardResponseDto::from)
                    .toList();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("errorMessage", "검색 중 문제가 발생하였습니다. 다시 시도해주세요."));
        }
    }


    // 요리방법 검색
    @GetMapping("/boards/method/{method}")
    public ResponseEntity<?> method(@PathVariable Method method,
                                    @RequestParam(defaultValue = "date") String sort) {

        try {
            List<BoardWithImageDto> boards;

            if ("likes".equals(sort)) {
                boards = boardService.findByMethodOrderByLikes(method);
            } else {
                boards = boardService.findByMethod(method);
            }

            List<BoardResponseDto> response = boards.stream()
                    .map(BoardResponseDto::from)
                    .toList();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("errorMessage", "검색 중 문제가 발생하였습니다. 다시 시도해주세요."));
        }
    }
}
