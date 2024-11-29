package com.cookingrecipe.cookingrecipe.controller;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.domain.Category;
import com.cookingrecipe.cookingrecipe.domain.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.domain.Method;
import com.cookingrecipe.cookingrecipe.dto.BoardSaveDto;
import com.cookingrecipe.cookingrecipe.dto.BoardUpdateDto;
import com.cookingrecipe.cookingrecipe.dto.BoardWithImageDto;
import com.cookingrecipe.cookingrecipe.dto.RecipeStepDto;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {


    private final BoardService boardService;
    private final LikeRepository likeRepository;


    // 게시글 작성 폼
    @GetMapping("/boards/new")
    public String saveForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", userDetails.getUser());

        BoardSaveDto form = new BoardSaveDto();
        form.setRecipeSteps(new ArrayList<>());
        model.addAttribute("form", form);

        return "board/save";
    }


    // 게시글 작성
    @PostMapping("/boards")
    public String save(@Validated @ModelAttribute("form") BoardSaveDto boardSaveDto, BindingResult bindingResult,
                       @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", userDetails.getUser());

        // 유효성 검사
        if (bindingResult.hasErrors()) {
            return "board/save";
        }

        try {
            // 성공
            Long boardId = boardService.save(boardSaveDto, boardSaveDto.getRecipeSteps(), userDetails);
            return "redirect:/boards/" + boardId;
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "board/save";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "게시글 저장 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "board/save";
        }
    }


    // 특정 게시글 조회
    @GetMapping("/boards/{id:\\d+}")
    public String view(@PathVariable("id") Long boardId,
                       @AuthenticationPrincipal CustomUserDetails userDetails,
                       Model model) {

        boardService.addViewCount(boardId);

        // 게시글과 속한 레시피 조회
        Board board = boardService.findByIdWithUser(boardId)
                .orElseThrow(() -> new BadRequestException("게시글을 찾을 수 없습니다"));

        // 좋아요, 북마크 초기 false 설정
        boolean isLiked = false;
        boolean isBookmarked = false;

        // 로그인 한 사용자의 좋아요, 북마크 여부 조회 후 설정
        if (userDetails != null) {
            isLiked = boardService.isLikedByUser(boardId, userDetails.getId());
            isBookmarked = boardService.isBookmarkedByUser(boardId, userDetails.getId());
        }

        // 랜덤 숫자 생성 (0 ~ 9999) -> 캐시 무효화를 위해
        int randomValue = (int) (Math.random() * 10000);

        // 모델에 필요한 데이터 추가
        model.addAttribute("board", board);
        model.addAttribute("recipes", board.getRecipeSteps());
        model.addAttribute("isLiked", isLiked);
        model.addAttribute("isBookmarked", isBookmarked);
        model.addAttribute("isLoggedIn", userDetails != null);
        model.addAttribute("authorId", board.getUser().getId()); // 작성자 ID 추가
        model.addAttribute("currentUserId", userDetails != null ? userDetails.getId() : null); // 현재 사용자 ID 추가
        model.addAttribute("randomValue", randomValue); // 랜덤 값 추가

        return "board/view";
    }




    @GetMapping("/boards/update/{id}")
    public String updateForm(@PathVariable Long id,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {

        // 게시글을 ID로 조회
        Board board = boardService.findByIdWithUser(id)
                .orElseThrow(() -> new BadRequestException("게시글을 찾을 수 없습니다"));

        if (board == null) {
            log.warn("게시글이 존재하지 않습니다. 게시글 ID: {}", id);  // 게시글이 없을 경우 경고 로그 출력
            model.addAttribute("errorMessage", "게시글을 찾을 수 없습니다.");
            return "error/404";  // 에러 페이지 반환
        }

        // 게시글 수정 폼에 사용할 DTO 생성
        BoardUpdateDto updateDto = BoardUpdateDto.builder()
                .title(board.getTitle())
                .ingredient(board.getIngredient())
                .content(board.getContent())
                .category(board.getCategory())
                .method(board.getMethod())
                .recipeSteps(board.getRecipeSteps().stream()
                        .map(step -> RecipeStepDto.builder()
                                .stepOrder(step.getStepOrder())
                                .description(step.getDescription())
                                .imagePath(step.getImagePath())
                                .build())
                        .collect(Collectors.toList())
                )
                .build();

        // 디버그 로그 추가
        log.warn("Loaded Board: {}", board);
        log.warn("Generated Update DTO: {}", updateDto);

        // board가 null이 아니면 모델에 전달
        model.addAttribute("board", board);
        model.addAttribute("form", updateDto);

        return "board/update";  // 수정 폼 페이지 반환
    }


    @PostMapping("/boards/update/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("form") BoardUpdateDto boardUpdateDto,
                         BindingResult bindingResult,
                         Model model) {

        // 디버그 로그 추가
        log.warn("Received BoardUpdateDto: {}", boardUpdateDto);
        log.warn("RecipeSteps: {}", boardUpdateDto.getRecipeSteps());

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "입력값을 확인해주세요.");
            return "board/update";
        }

        try {
            // 게시글 업데이트
            boardService.update(id, boardUpdateDto, boardUpdateDto.getRecipeSteps());

            return "redirect:/boards/" + id;
        } catch (IOException e) {
            model.addAttribute("errorMessage", "파일 처리 중 오류가 발생했습니다.");
            return "board/update";
        }
    }



    // 게시글 좋아요 토글
    @PatchMapping("/boards/{id}/like")
    public String like(@PathVariable("id") Long boardId,
                       @AuthenticationPrincipal CustomUserDetails userDetails) {


        // 비로그인 -> 로그인 화면으로 리다이렉트
        if (userDetails == null) {
            return "redirect:/login";
        }

        boardService.toggleLike(boardId, userDetails.getId());

        return "redirect:/boards/" + boardId;
    }


    // 게시글 북마크 토글
    @PatchMapping("/boards/{id}/bookmark")
    public String toggleBookmark(@PathVariable("id") Long boardId,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        boardService.toggleBookmark(boardId, userDetails.getId());
        return "redirect:/boards/" + boardId;
    }


//    // 게시글 삭제
//    @PostMapping("/boards/delete/{id}")
//    public String delete(@PathVariable("id") Long id,
//                         @AuthenticationPrincipal CustomUserDetails userDetails,
//                         RedirectAttributes redirectAttributes,
//                         Model model) {
//
//        boardService.deleteById(id);
//
//
//    }


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


    // 전체 레시피 조회
    @GetMapping("/boards")
    public String all(Model model) {

        List<BoardWithImageDto> boards = boardService.findAllByDateDesc();

        model.addAttribute("boards", boards);
        return "board/all";
    }


    // 전체 레시피 TOP 10
    @GetMapping("/boards/top")
    public String top(Model model) {

        List<BoardWithImageDto> boards = boardService.findTopRecipesByLikes(10);

        model.addAttribute("boards", boards);
        return "board/top";
    }


    // 이달의 레시피 TOP 10
    @GetMapping("/boards/monthly")
    public String monthly(Model model) {

        List<BoardWithImageDto> boards = boardService.findMonthlyRecipesByLikes(10);
        model.addAttribute("boards", boards);
        return "board/monthly";
    }


    // 카테고리 검색
    @GetMapping("/boards/category/{category}")
    public String category(@PathVariable Category category,
                           @RequestParam(defaultValue = "date") String sort,
                           Model model) {

        List<BoardWithImageDto> boards;

        if ("likes".equals(sort)) {
            boards = boardService.findByCategoryOrderByLikes(category);
        } else {
            boards = boardService.findByCategory(category);
        }

        model.addAttribute("boards", boards);
        model.addAttribute("category", category);
        model.addAttribute("sort", sort);
        return "board/category";
    }


    // 요리방법 검색
    @GetMapping("/boards/method/{method}")
    public String method(@PathVariable Method method,
                         @RequestParam(defaultValue = "date") String sort,
                         Model model) {

        List<BoardWithImageDto> boards;

        if ("likes".equals(sort)) {
            boards = boardService.findByMethodOrderByLikes(method);
        } else {
            boards = boardService.findByMethod(method);
        }

        model.addAttribute("boards", boards);
        model.addAttribute("method", method);
        model.addAttribute("sort", sort);
        return "board/method";
    }
}
