package com.project.blog.domain.post.dto.response;

import com.project.blog.domain.post.entity.Post;
import com.project.blog.global.enums.PostVisibility;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {

    private final Long postId;
    private final String title;
    private final String content;
    private final int views;
    private final long likes;
    private final String userNickname;
    private final String postVisibility;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PostResponseDto(
            Long postId,
            String title,
            String content,
            int views,
            long likes,
            String userNickname,
            String postVisibility,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.views = views;
        this.likes = likes;
        this.userNickname = userNickname;
        this.postVisibility = postVisibility;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PostResponseDto fromEntity(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViews(),
                post.getPostLikes().size(),
                post.getUser().getNickname(),
                post.getPostVisibility().getValue(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }


}
