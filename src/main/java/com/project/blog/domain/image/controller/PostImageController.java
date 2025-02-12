package com.project.blog.domain.image.controller;

import com.project.blog.domain.image.dto.PostImageResponseDto;
import com.project.blog.domain.image.service.PostImageService;
import com.project.blog.global.base.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class PostImageController {

    private final PostImageService postImageService;

    // 이미지 업로드
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> S3Upload(
            @RequestPart(required = false) MultipartFile image
    ) {
        PostImageResponseDto profileImage = postImageService.upload(image);

        ApiResponse result = ApiResponse.created(profileImage);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 이미지 삭제
    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<Void> S3Delete(
            @PathVariable Long imageId
    ) {
        postImageService.deleteImageFromS3(imageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
