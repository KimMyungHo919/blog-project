package com.project.blog.domain.user.dto.request;

import jakarta.annotation.Nullable;
import lombok.Getter;

@Getter
public class UserChangeProfileRequestDto {

    private final String password;
    private final String nickname;

    @Nullable
    private final Long imageId;

    @Nullable
    private final String profileImageUrl;

    public UserChangeProfileRequestDto(String password, String nickname, @Nullable Long imageId, @Nullable String profileImageUrl) {
        this.password = password;
        this.nickname = nickname;
        this.imageId = imageId;
        this.profileImageUrl = profileImageUrl;
    }
}
