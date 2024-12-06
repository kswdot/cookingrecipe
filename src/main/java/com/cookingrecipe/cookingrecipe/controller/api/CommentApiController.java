package com.cookingrecipe.cookingrecipe.controller.api;

import com.cookingrecipe.cookingrecipe.domain.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.dto.CommentRequestDto;
import com.cookingrecipe.cookingrecipe.dto.CommentResponseDto;
import com.cookingrecipe.cookingrecipe.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentApiController {


    private final CommentService commentService;


    // 댓글 작성
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CommentRequestDto commentRequestDto,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 로그인된 사용자 ID 가져오기
        Long userId = userDetails.getUser().getId();
        commentRequestDto.setUserId(userId);

        CommentResponseDto createdComment = commentService.create(commentRequestDto);
        return ResponseEntity.ok(createdComment);
    }


    // 댓글 수정
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CommentRequestDto commentRequestDto,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 로그인된 사용자 ID 가져오기
        Long userId = userDetails.getUser().getId();
        commentRequestDto.setUserId(userId);

        CommentResponseDto updatedComment = commentService.update(id, commentRequestDto);
        return ResponseEntity.ok(updatedComment);
    }


    // 댓글 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUser().getId();

        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
