package com.project.blog.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserDeleteRequestDto {

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 최소 1글자 이상 포함하고 8자 이상이어야 합니다."
    )
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(max = 15, message = "비밀번호는 최대 15글자 입니다.")
    private final String password;

    public UserDeleteRequestDto(String password) {
        this.password = password;
    }
}
