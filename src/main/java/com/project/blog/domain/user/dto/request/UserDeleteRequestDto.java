package com.project.blog.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserDeleteRequestDto {

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 최소 1글자 이상 포함하고 8자 이상이어야 합니다."
    )
    @NotBlank
    private final String password;

    public UserDeleteRequestDto(String password) {
        this.password = password;
    }
}
