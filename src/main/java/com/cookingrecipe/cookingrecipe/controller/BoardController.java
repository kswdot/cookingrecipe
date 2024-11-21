package com.cookingrecipe.cookingrecipe.controller;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.domain.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.domain.RecipeStep;
import com.cookingrecipe.cookingrecipe.dto.BoardSaveDto;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
import com.cookingrecipe.cookingrecipe.repository.LikeRepository;
import com.cookingrecipe.cookingrecipe.service.BoardService;
import com.cookingrecipe.cookingrecipe.service.RecipeStepService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {


    private final BoardService boardService;
    private final RecipeStepService recipeStepService;
    private final LikeRepository likeRepository;


    // 게시글 작성 폼
    @GetMapping("/board")
    public String saveForm(@ModelAttribute("form") BoardSaveDto boardSaveDto,
                           @AuthenticationPrincipal CustomUserDetails userDetails) {

        boardSaveDto.setNickname(userDetails.getNickname());
        return "board/save";
    }


//    // 게시글 작성
//    @PostMapping("/board")
//    public String save(@ModelAttribute("form") BoardSaveDto boardSaveDto,
//                       BindingResult bindingResult,
//                       @RequestParam("images") List<MultipartFile> images,
//                       @AuthenticationPrincipal CustomUserDetails userDetails,
//                       RedirectAttributes redirectAttributes) {
//
//        if (bindingResult.hasErrors()) {
//            redirectAttributes.addFlashAttribute("errorMessage", "입력 값을 확인해주세요");
//            return "redirect:/board";
//        }
//
//        try {
//            // 디버깅: userDetails 정보 출력
//            System.out.println("로그인된 사용자 ID: " + userDetails.getId());
//
//            boardSaveDto.setUserId(userDetails.getId());
//
//            // 디버깅: DTO 정보 출력
//            System.out.println("DTO 정보: " + boardSaveDto);
//
//            // 게시글 저장 후 ID 반환
//            Long boardId = boardService.save(boardSaveDto, images, userDetails);
//
//            // 상세 페이지로 리다이렉트
//            return "redirect:/board/" + boardId;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            redirectAttributes.addFlashAttribute("errorMessage", "작성 중 문제가 발생했습니다. 다시 시도해주세요.");
//            return "redirect:/board"; // 에러 처리 페이지로 리다이렉트
//        }
//    }


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
