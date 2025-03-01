package com.project.blog.domain.user.dto.response;

import com.project.blog.domain.user.entity.User;
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

    public static UserFriendsResponseDto fromEntity(User friendUser) {
        return new UserFriendsResponseDto(
                friendUser.getId(),
                friendUser.getNickname(),
                friendUser.getEmail()
        );
    }
}
