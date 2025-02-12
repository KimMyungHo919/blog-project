package com.project.blog.domain.image.dto;

import lombok.Getter;

@Getter
public class PostImageResponseDto {

    private final Long imageId;
    private final String publicUrl;


    public PostImageResponseDto(Long imageId, String publicUrl) {
        this.imageId = imageId;
        this.publicUrl = publicUrl;
    }
}
