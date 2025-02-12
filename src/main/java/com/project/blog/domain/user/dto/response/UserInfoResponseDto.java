package com.project.blog.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserInfoResponseDto {

    private final Long id;
    private final String email;
    private final String nickname;
    private final Long imageId;
    private final String profileImage;

    public UserInfoResponseDto(Long id, String email, String nickname, Long imageId, String profileImage) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.imageId = imageId;
        this.profileImage = profileImage;
    }
}
