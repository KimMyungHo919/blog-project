package com.project.blog.domain.friend.dto;

import com.project.blog.global.enums.FriendStatus;
import lombok.Getter;

@Getter
public class FriendReceivedResponseDto {

    private final Long userId;
    private final String userEmail;
    private final String userNickname;
    private final FriendStatus friendStatus;

    public FriendReceivedResponseDto(Long userId, String userEmail, String userNickname, FriendStatus friendStatus) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userNickname = userNickname;
        this.friendStatus = friendStatus;
    }
}
