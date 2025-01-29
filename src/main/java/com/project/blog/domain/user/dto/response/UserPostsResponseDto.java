package com.project.blog.domain.user.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserPostsResponseDto {

    private final String title;
    private final String content;
    private final String userNickname;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public UserPostsResponseDto(
            String title,
            String content,
            String userNickname,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.title = title;
        this.content = content;
        this.userNickname = userNickname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
