package com.cookingrecipe.cookingrecipe.controller.api;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.domain.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.dto.BoardResponseDto;
import com.cookingrecipe.cookingrecipe.dto.BoardSaveDto;
import com.cookingrecipe.cookingrecipe.dto.BoardUpdateDto;
import com.cookingrecipe.cookingrecipe.dto.BoardWithImageDto;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
import com.cookingrecipe.cookingrecipe.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardApiController {


    private final BoardService boardService;


    // 전체 게시글 조회
    @GetMapping("")
    public ResponseEntity<?> all() {

        try {
            List<BoardWithImageDto> boards = boardService.findAllByDateDesc();

            List<BoardResponseDto> response = boards.stream()
                    .map(BoardResponseDto::from)
                    .toList();

            return ResponseEntity.ok().body(response);
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
                       @AuthenticationPrincipal CustomUserDetails userDetails,
                       HttpSession session) {

        Long userId = userDetails != null ? userDetails.getId() : 0L; // 로그인되지 않은 경우 0L로 처리

        boolean isNewView = boardService.addViewCountWithRedis(boardId, userId);

        // 게시글과 속한 레시피 조회
        Board board = boardService.findByIdWithUser(boardId)
                .orElseThrow(() -> new BadRequestException("게시글을 찾을 수 없습니다"));

        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("board", board);
        response.put("viewCount", board.getView()); // DB 조회
        response.put("likeCount", board.getLikeCount()); // 좋아요 수
        response.put("isNewView", isNewView);


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
    @PatchMapping("/boards/{id}/bookmark")
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
    @GetMapping("/boards/search")
    public String search(@RequestParam(required = false) String searchCriteria,
                         @RequestParam(required = false) String keyword,
                         @RequestParam(defaultValue = "date") String sort,
                         Model model) {

        if (keyword == null || keyword.trim().isEmpty()) {
            model.addAttribute("errorMessage", "검색어를 입력하세요");
            return "board/search";
        }

        List<BoardWithImageDto> boards;
        if ("likes".equals(sort)) {
            boards = boardService.searchBoardsOrderByLikes(searchCriteria, keyword);
        } else {
            boards = boardService.searchBoards(searchCriteria, keyword);
        }

        model.addAttribute("boards", boards);
        model.addAttribute("searchCriteria", searchCriteria);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);

        return "board/search";
    }

}
