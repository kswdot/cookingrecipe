package com.cookingrecipe.cookingrecipe.controller;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.domain.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.dto.BoardSaveDto;
import com.cookingrecipe.cookingrecipe.repository.LikeRepository;
import com.cookingrecipe.cookingrecipe.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class BoardController {


    private final BoardService boardService;
    private final LikeRepository likeRepository;


    // 게시글 작성 폼
    @GetMapping("/board")
    public String saveForm(@ModelAttribute("form") BoardSaveDto boardSaveDto,
                           @AuthenticationPrincipal CustomUserDetails userDetails) {

        boardSaveDto.setNickname(userDetails.getNickname());
        return "board/save";
    }


    // 게시글 작성
    @PostMapping("/board")
    public String save() {
            return "redirect:/board"; // 에러 처리 페이지로 리다이렉트
    }


    // 특정 게시글 조회
    @GetMapping("/board/{id}")
    public String viewBoard(@PathVariable("id") Long boardId,
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
