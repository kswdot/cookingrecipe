package com.cookingrecipe.cookingrecipe.controller.api;

import com.cookingrecipe.cookingrecipe.domain.User;
import com.cookingrecipe.cookingrecipe.dto.BoardWithImageDto;
import com.cookingrecipe.cookingrecipe.dto.api.BoardResponseDto;
import com.cookingrecipe.cookingrecipe.dto.api.UserResponseDto;
import com.cookingrecipe.cookingrecipe.service.Board.BoardService;
import com.cookingrecipe.cookingrecipe.service.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminApiController {


    private final BoardService boardService;
    private final UserService userService;


    // 게시글 목록 전체 조회
    @GetMapping("/boards")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<BoardResponseDto>> getAllBoards() {

        List<BoardWithImageDto> boards = boardService.findAll();
        List<BoardResponseDto> response = boards.stream()
                .map(BoardResponseDto::from)
                .toList();

        return ResponseEntity.ok(response);
    }


    // 특정 게시글 삭제
    @DeleteMapping("/boards/{boardId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteById(boardId);
        return ResponseEntity.ok("사용자를 성공적으로 삭제했습니다.");
    }


    // 사용자 목록 전체 조회
    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {

        List<User> users = userService.findAll();
        List<UserResponseDto> response = users.stream()
                .map(UserResponseDto::from)
                .toList();

        return ResponseEntity.ok(response);
    }


    // 특정 사용자 삭제
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteById(userId); // 서비스 메서드 필요
        return ResponseEntity.ok("사용자를 성공적으로 삭제했습니다.");
    }
}
