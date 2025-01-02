package com.cookingrecipe.cookingrecipe.controller;

import com.cookingrecipe.cookingrecipe.dto.Board.BoardDto;
import com.cookingrecipe.cookingrecipe.service.Board.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final BoardService boardService;

    @GetMapping
    public String index(@PageableDefault(size = 9, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
                        Model model) {
        Page<BoardDto> boardPage = boardService.findAllByDateDesc(pageable);

        model.addAttribute("boards", boardPage.getContent());
        model.addAttribute("page", boardPage);
        return "index";
    }

}
