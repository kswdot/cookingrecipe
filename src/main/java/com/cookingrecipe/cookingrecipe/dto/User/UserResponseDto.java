package com.cookingrecipe.cookingrecipe.dto.User;

import com.cookingrecipe.cookingrecipe.domain.User.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String loginId;
    private String nickname;
    private String email;
    private String role; // 사용자 권한
    private LocalDateTime createdDate; // 가입일

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getLoginId(),
                user.getNickname(),
                user.getEmail(),
                user.getRole().name(), // Enum to String
                user.getCreatedDate() // LocalDateTime to String
        );
    }
}
