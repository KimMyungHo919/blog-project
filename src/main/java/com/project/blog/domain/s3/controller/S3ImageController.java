package com.project.blog.domain.s3.controller;

import com.project.blog.domain.s3.dto.ImageResponseDto;
import com.project.blog.domain.s3.service.S3ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3ImageController {

    private final S3ImageService s3ImageService;

    // 이미지 업로드
    @PostMapping("/upload")
    public ResponseEntity<ImageResponseDto> S3Upload(
            @RequestPart(required = false) MultipartFile image
    ) {
        ImageResponseDto profileImage = s3ImageService.upload(image);
        return ResponseEntity.status(HttpStatus.CREATED).body(profileImage);
    }

    // 이미지 삭제
    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<Void> S3Delete(
            @PathVariable Long imageId
    ) {
        s3ImageService.deleteImageFromS3(imageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
