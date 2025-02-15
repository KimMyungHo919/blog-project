package com.project.blog.domain.user.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import jakarta.validation.constraints.Pattern;

@Getter
public class UserSignupRequestDto {


    @Email
    @NotBlank(message = "이메일을 입력해주세요.")
    private final String email;

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 최소 1글자 이상 포함하고 8자 이상이어야 합니다."
    )
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private final String password;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private final String nickname;

    @Nullable
    private final Long imageId;

    @Nullable
    private final String profileImageUrl;

    @Pattern(regexp = "일반유저|관리자", message = "'일반유저' 혹은 '관리자' 만 입력가능합니다.")
    private final String role;

    public UserSignupRequestDto(
            String email,
            String password,
            String nickname,
            String role,
            @Nullable Long imageId,
            @Nullable String profileImageUrl
    ) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.imageId = imageId;
        this.profileImageUrl = profileImageUrl;
    }
}
