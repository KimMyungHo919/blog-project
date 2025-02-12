package com.project.blog.domain.user.dto.request;

import lombok.Getter;

@Getter
public class UserChangeProfileRequestDto {

    private final String password;
    private final String nickname;
    private final Long imageId;
    private final String profileImage;

    public UserChangeProfileRequestDto(String password, String nickname, Long imageId, String profileImage) {
        this.password = password;
        this.nickname = nickname;
        this.imageId = imageId;
        this.profileImage = profileImage;
    }
}
