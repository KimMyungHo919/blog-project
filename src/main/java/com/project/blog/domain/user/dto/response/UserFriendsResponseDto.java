package com.project.blog.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserFriendsResponseDto {

    private final Long userId;
    private final String userNickname;
    private final String userEmail;

    public UserFriendsResponseDto(Long userId, String userNickname, String userEmail) {
        this.userId = userId;
        this.userNickname = userNickname;
        this.userEmail = userEmail;
    }
}
