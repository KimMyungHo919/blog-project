package com.project.blog.domain.user.dto.response;

import com.project.blog.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserSignupResponseDto {

    private final Long id;
    private final String email;
    private final String nickname;

    public UserSignupResponseDto(Long id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }

    public static UserSignupResponseDto fromEntity(User user) {
        return new UserSignupResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );
    }

}
