package com.cookingrecipe.cookingrecipe.controller.api;

import com.cookingrecipe.cookingrecipe.dto.LoginRequestDto;
import com.cookingrecipe.cookingrecipe.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginRequestDto loginRequestDto,
                                   BindingResult bindingResult) {

        // 유효성 검사
        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> Map.of(
                            "field", error.getField(),
                            "message", error.getDefaultMessage()
                    ))
                    .toList();

            return ResponseEntity.badRequest().body(errorMessages);
        }


        try {
            // 사용자 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getLoginId(), loginRequestDto.getPassword())
            );

            // JWT 토큰 생성
            String token = jwtTokenProvider.generateToken(authentication.getName());

            // JWT 반환
            return ResponseEntity.ok(Map.of("token", token));
        } catch (AuthenticationException e) {

            // 인증 실패
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "아이디, 비밀번호를 다시 확인해주세요."));
        }
    }


}
