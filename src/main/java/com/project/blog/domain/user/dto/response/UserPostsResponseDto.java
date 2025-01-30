package com.project.blog.domain.user.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserPostsResponseDto {

    private final Long postId;
    private final String title;
    private final String content;
    private final String userNickname;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public UserPostsResponseDto(
            Long postId,
            String title,
            String content,
            String userNickname,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.userNickname = userNickname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
