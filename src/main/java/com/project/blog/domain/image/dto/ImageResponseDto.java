package com.project.blog.domain.image.dto;

import lombok.Getter;

@Getter
public class ImageResponseDto {

    private final Long imageId;
    private final String publicUrl;


    public ImageResponseDto(Long imageId, String publicUrl) {
        this.imageId = imageId;
        this.publicUrl = publicUrl;
    }
}
