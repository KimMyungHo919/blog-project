package com.project.blog.domain.image.dto;

import lombok.Getter;

@Getter
public class ImageRequestDto {

    private final String addr;

    public ImageRequestDto(String addr) {
        this.addr = addr;
    }
}
