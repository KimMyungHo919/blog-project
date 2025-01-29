package com.project.blog.domain.user.dto.request;

import lombok.Getter;

@Getter
public class UserChangeNicknameDto {

    private final String password;
    private final String nickname;

    public UserChangeNicknameDto(String password, String nickname) {
        this.password = password;
        this.nickname = nickname;
    }
}
