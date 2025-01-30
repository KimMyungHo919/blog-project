package com.project.blog.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CommentUpdateRequestDto {

    @NotBlank(message = "댓글을 입력해주세요.")
    @Size(min = 1, max = 100, message = "댓글은 1~100 글자로 입력해주세요.")
    private final String comment;

    public CommentUpdateRequestDto(String comment) {
        this.comment = comment;
    }
}
