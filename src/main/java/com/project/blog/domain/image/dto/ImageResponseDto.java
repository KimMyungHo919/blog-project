package com.project.blog.domain.image.dto;

import lombok.Getter;

@Getter
public class ImageResponseDto {

    private final Long imageId;
    private final String publicUrl;
    private final Long fileSize;
    private final String originalFileName;
    private final String imageType;


    public ImageResponseDto(Long imageId, String publicUrl, Long fileSize, String originalFileName, String imageType) {
        this.imageId = imageId;
        this.publicUrl = publicUrl;
        this.fileSize = fileSize;
        this.originalFileName = originalFileName;
        this.imageType = imageType;
    }
}
