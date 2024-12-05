package com.cookingrecipe.cookingrecipe.controller.api;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.domain.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.dto.BoardSaveDto;
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
            return ResponseEntity.ok().body(boards);
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
        response.put("viewCount", board.getView()); // DB에서 가져온 조회수
        response.put("likeCount", board.getLikeCount()); // 좋아요 수

        return ResponseEntity.ok(response);
    }
}
