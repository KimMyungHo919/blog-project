package com.project.blog.domain.user.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupResponseDto {

    private final Long id;
    private final String email;
    private final String nickName;

    public UserSignupResponseDto(Long id, String email, String nickName) {
        this.id = id;
        this.email = email;
        this.nickName = nickName;
    }
}
