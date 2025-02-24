package com.project.blog.domain.user.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserChangeProfileRequestDto {

    private final String password;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 1, message = "닉네임은 최소 한글자 이상이어야 합니다.")
    @Size(max = 15, message = "비밀번호는 최대 15글자 입니다.")
    private final String nickname;

    @Nullable
    private final Long imageId;

    @Nullable
    private final String profileImageUrl;

    public UserChangeProfileRequestDto(String password, String nickname, @Nullable Long imageId, @Nullable String profileImageUrl) {
        this.password = password;
        this.nickname = nickname;
        this.imageId = imageId;
        this.profileImageUrl = profileImageUrl;
    }
}
