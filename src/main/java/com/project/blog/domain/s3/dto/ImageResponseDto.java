package com.project.blog.domain.s3.dto;

import lombok.Getter;

@Getter
public class ImageResponseDto {

    private final Long imageId;
    private final String imageUrl;


    public ImageResponseDto(Long imageId, String imageUrl) {
        this.imageId = imageId;
        this.imageUrl = imageUrl;
    }
}
