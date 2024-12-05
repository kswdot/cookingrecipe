package com.cookingrecipe.cookingrecipe.controller;

import com.cookingrecipe.cookingrecipe.domain.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.dto.CommentRequestDto;
import com.cookingrecipe.cookingrecipe.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class CommentController {


    private final CommentService commentService;


    // 댓글 작성
    @PostMapping("/comments/create")
    public String create(@ModelAttribute CommentRequestDto commentRequestDto,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 로그인된 사용자 ID 가져오기
        Long userId = userDetails.getUser().getId();
        commentRequestDto.setUserId(userId);

        commentService.create(commentRequestDto);
        return "redirect:/boards/" + commentRequestDto.getBoardId();
    }


    // 댓글 수정
    @PostMapping("/comments/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute CommentRequestDto commentRequestDto,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 로그인된 사용자 ID 가져오기
        Long userId = userDetails.getUser().getId();
        commentRequestDto.setUserId(userId);

        commentService.update(id, commentRequestDto);
        return "redirect:/boards/" + commentRequestDto.getBoardId();
    }


    // 댓글 삭제
    @PostMapping("/comments/delete/{id}")
    public String delete(@PathVariable Long id, @RequestParam Long boardId,
                         @ModelAttribute CommentRequestDto commentRequestDto,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 로그인된 사용자 ID 가져오기
        Long userId = userDetails.getUser().getId();
        commentRequestDto.setUserId(userId); // DTO에 사용자 ID 설정

        commentService.delete(id);
        return "redirect:/boards/" + boardId;
    }
}
