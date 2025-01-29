package com.project.blog.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserInfoResponseDto {

    private final Long id;
    private final String email;
    private final String nickname;

    public UserInfoResponseDto(Long id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }
}
