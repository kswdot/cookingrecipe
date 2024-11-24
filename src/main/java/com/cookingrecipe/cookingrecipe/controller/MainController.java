package com.cookingrecipe.cookingrecipe.controller;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.dto.BoardWithImageDto;
import com.cookingrecipe.cookingrecipe.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final BoardService boardService;

    @GetMapping
    public String index(Model model) {
        List<BoardWithImageDto> boards = boardService.findAllByDateDesc();

        model.addAttribute("boards", boards);
        return "index";
    }

}
