package com.cookingrecipe.cookingrecipe.controller;

import com.cookingrecipe.cookingrecipe.domain.Board;
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
        List<Board> boards = boardService.findAllByDateDesc();
        boards.forEach(board -> {
            System.out.println("Board Title: " + board.getTitle());
            board.getImages().forEach(image -> System.out.println("Image Path: " + image.getPath()));
        });
        model.addAttribute("recipes", boards);
        return "index";
    }

}
