package com.cookingrecipe.cookingrecipe.controller;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.domain.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.dto.BoardSaveDto;
import com.cookingrecipe.cookingrecipe.dto.RecipeStepDto;
import com.cookingrecipe.cookingrecipe.exception.UserNotFoundException;
import com.cookingrecipe.cookingrecipe.repository.LikeRepository;
import com.cookingrecipe.cookingrecipe.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {


    private final BoardService boardService;
    private final LikeRepository likeRepository;


    // 게시글 작성 폼
    @GetMapping("/board")
    public String saveForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        model.addAttribute("user", userDetails.getUser());

        BoardSaveDto form = new BoardSaveDto();
        form.setRecipeSteps(new ArrayList<>());
        model.addAttribute("form", form);

        return "board/save";
    }


    // 게시글 작성
    @PostMapping("/board")
    public String save(@Validated @ModelAttribute("form") BoardSaveDto boardSaveDto, BindingResult bindingResult,
                       @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        model.addAttribute("user", userDetails.getUser());

        // 유효성 검사
        if (bindingResult.hasErrors()) {
            return "board/save";
        }

        try {
            // 성공
            Long boardId = boardService.save(boardSaveDto, boardSaveDto.getRecipeSteps(), userDetails);
            return "redirect:/board/" + boardId;
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "board/save";
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            model.addAttribute("errorMessage", "게시글 저장 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "board/save";
        }
    }


    // 특정 게시글 조회
    @GetMapping("/board/{id}")
    public String view(@PathVariable("id") Long boardId,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            Model model) {

        // 게시글과 속한 레시피 조회
        Board board = boardService.findBoardWithRecipeSteps(boardId);

        // 좋아요, 북마크 초기 false 설정
        boolean isLiked = false;
        boolean isBookmarked = false;

        // 로그인 한 사용자의 좋아요, 북마크 여부 조회 후 설정
        if (userDetails != null) {
            isLiked = boardService.isLikedByUser(boardId, userDetails.getId());
            isBookmarked = boardService.isBookmarkedByUser(boardId, userDetails.getId());
        }


        model.addAttribute("board", board);
        model.addAttribute("recipes", board.getRecipeSteps());
        model.addAttribute("isLiked", isLiked);
        model.addAttribute("isBookmarked", isBookmarked);
        model.addAttribute("isLoggedIn", userDetails != null);

        return "board/view";
    }


    // 게시글 좋아요 토글
    @PostMapping("/board/{id}/like")
    public String like(@PathVariable("id") Long boardId,
                       @AuthenticationPrincipal CustomUserDetails userDetails) {


        // 비로그인 -> 로그인 화면으로 리다이렉트
        if (userDetails == null) {
            return "redirect:/login";
        }


        boardService.toggleLike(boardId, userDetails.getId());

        return "redirect:/board/" + boardId;
    }


    // 게시글 북마크 토글
    @PostMapping("/board/{id}/bookmark")
    public String toggleBookmark(@PathVariable("id") Long boardId,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        boardService.toggleBookmark(boardId, userDetails.getId());
        return "redirect:/board/" + boardId;
    }
}
