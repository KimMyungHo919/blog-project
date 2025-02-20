package com.project.blog.domain.user.dto.response;

import com.project.blog.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserCommentResponseDto {

    private final Long commentId;
    private final String comment;
    private final String userNickname;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public UserCommentResponseDto(Long commentId, String comment, String userNickname, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.commentId = commentId;
        this.comment = comment;
        this.userNickname = userNickname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserCommentResponseDto fromEntity(Comment comment) {
        return new UserCommentResponseDto(
                comment.getId(),
                comment.getComment(),
                comment.getUser().getNickname(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }

}
