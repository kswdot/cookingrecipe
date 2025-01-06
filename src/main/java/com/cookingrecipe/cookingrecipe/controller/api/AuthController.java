package com.cookingrecipe.cookingrecipe.controller.api;

import com.cookingrecipe.cookingrecipe.dto.User.LoginRequestDto;
import com.cookingrecipe.cookingrecipe.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;


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

            // Redis에서 기존 토큰 확인
            String existingToken = (String) redisTemplate.opsForValue().get(authentication.getName());
            if (existingToken != null) {
                // 기존 토큰 반환
                return ResponseEntity.ok(Map.of("token", existingToken));
            }

            // 기존 토큰이 없으면 새로 생성
            String token = jwtTokenProvider.generateToken(authentication.getName());

            // Redis에 새 JWT 저장 (유효기간 설정)
            redisTemplate.opsForValue().set(
                    authentication.getName(),
                    token,
                    3600000,                  // 만료 시간: 1시간 (3600000ms)
                    TimeUnit.MILLISECONDS
            );

            // 새 토큰 반환
            return ResponseEntity.ok(Map.of("token", token));
        } catch (AuthenticationException e) {

            // 인증 실패
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "아이디, 비밀번호를 다시 확인해주세요."));
        }
    }

    // JWT + Redis 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        // JWT에서 사용자 이름 추출
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));

        // Redis 작업 없이 로그아웃 완료 응답
        return ResponseEntity.ok(Map.of("message", "로그아웃이 완료되었습니다."));
    }


}
