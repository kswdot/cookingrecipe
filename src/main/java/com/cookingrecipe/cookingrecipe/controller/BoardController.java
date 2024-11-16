package com.cookingrecipe.cookingrecipe.controller;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.domain.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.dto.BoardSaveDto;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
import com.cookingrecipe.cookingrecipe.service.BoardService;
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


    // 게시글 작성 폼
    @GetMapping("/board")
    public String saveForm(@ModelAttribute("form") BoardSaveDto boardSaveDto) {

        return "board/save";
    }


    // 게시글 작성
    @PostMapping("/board")
    public String save(@ModelAttribute("form") BoardSaveDto boardSaveDto,
                       BindingResult bindingResult,
                       @RequestParam("images") List<MultipartFile> images,
                       @AuthenticationPrincipal CustomUserDetails userDetails,
                       RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "입력 값을 확인해주세요");
            return "redirect:/board";
        }

        try {
            // 디버깅: userDetails 정보 출력
            System.out.println("로그인된 사용자 ID: " + userDetails.getId());

            boardSaveDto.setUserId(userDetails.getId());

            // 디버깅: DTO 정보 출력
            System.out.println("DTO 정보: " + boardSaveDto);

            // 게시글 저장 후 ID 반환
            Long boardId = boardService.save(boardSaveDto, images, userDetails);

            // 상세 페이지로 리다이렉트
            return "redirect:/board/" + boardId;

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "작성 중 문제가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/board"; // 에러 처리 페이지로 리다이렉트
        }
    }


    // 특정 게시글 조회
    @GetMapping("/board/{id}")
    public String viewBoard(@PathVariable("id") Long boardId, Model model) {

        Board board = boardService.findByUser(boardId)
                .orElseThrow(() -> new BadRequestException("해당 게시글이 없습니다."));

        model.addAttribute("board", board);
        return "board/view";
    }


}
