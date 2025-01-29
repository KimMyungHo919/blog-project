package com.project.blog.domain.user.dto.response;

import com.project.blog.global.enums.Role;
import lombok.Getter;

@Getter
public class UserLoginResponseDto {

    private final Long id;
    private final String email;
    private final String nickname;
    private final Role role;

    public UserLoginResponseDto(Long id, String email, String nickname, Role role) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }
}
