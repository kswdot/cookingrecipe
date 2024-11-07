package com.cookingrecipe.cookingrecipe.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping
    public String index(HttpServletRequest request, Model model) {
        model.addAttribute("httpServletRequest", request);
        return "index";
    }
}
