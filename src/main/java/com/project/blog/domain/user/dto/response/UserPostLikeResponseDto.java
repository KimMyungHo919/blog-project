package com.project.blog.domain.user.dto.response;

import com.project.blog.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserPostLikeResponseDto {

    private final Long postId;
    private final String postTitle;
    private final String postContent;
    private final LocalDateTime postCreatedAt;
    private final LocalDateTime postUpdatedAt;

    public UserPostLikeResponseDto(
            Long postId,
            String postTitle,
            String postContent,
            LocalDateTime postCreatedAt,
            LocalDateTime postUpdatedAt
    ) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postCreatedAt = postCreatedAt;
        this.postUpdatedAt = postUpdatedAt;
    }

    public static UserPostLikeResponseDto fromEntity(Post post) {
        return new UserPostLikeResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

}
