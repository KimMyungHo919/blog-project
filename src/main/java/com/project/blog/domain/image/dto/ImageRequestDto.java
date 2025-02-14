package com.project.blog.domain.image.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ImageRequestDto {

    @NotBlank(message = "삭제할 이미지 URL 을 입력해주세요.")
    private final String addr;

    public ImageRequestDto(String addr) {
        this.addr = addr;
    }
}
