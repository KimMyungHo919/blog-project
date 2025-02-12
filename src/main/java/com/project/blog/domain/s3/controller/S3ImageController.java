package com.project.blog.domain.s3.controller;

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
    public ResponseEntity<String> S3Upload(
            @RequestPart(required = false) MultipartFile image
    ) {
        String profileImage = s3ImageService.upload(image);
        return ResponseEntity.status(HttpStatus.CREATED).body(profileImage);
    }

    // 이미지 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Void> S3Delete(
            @RequestParam String imageAddress
    ) {
        s3ImageService.deleteImageFromS3(imageAddress);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
