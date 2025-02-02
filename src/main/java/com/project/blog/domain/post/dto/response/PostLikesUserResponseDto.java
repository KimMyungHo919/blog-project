package com.project.blog.domain.post.dto.response;

import lombok.Getter;

@Getter
public class PostLikesUserResponseDto {

    private final String userNickname;

    public PostLikesUserResponseDto(String userNickname) {
        this.userNickname = userNickname;
    }
}
