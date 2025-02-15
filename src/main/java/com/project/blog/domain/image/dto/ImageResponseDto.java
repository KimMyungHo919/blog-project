package com.project.blog.domain.image.dto;

import lombok.Getter;

@Getter
public class ImageResponseDto {

    private final Long imageId;
    private final String publicUrl;
    private final String imageType;


    public ImageResponseDto(Long imageId, String publicUrl, String imageType) {
        this.imageId = imageId;
        this.publicUrl = publicUrl;
        this.imageType = imageType;
    }
}
