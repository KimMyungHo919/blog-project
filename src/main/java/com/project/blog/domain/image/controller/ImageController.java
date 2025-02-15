package com.project.blog.domain.image.controller;

import com.project.blog.domain.image.dto.ImageRequestDto;
import com.project.blog.domain.image.dto.ImageResponseDto;
import com.project.blog.domain.image.service.ImageService;
import com.project.blog.global.base.ApiResponse;
import com.project.blog.global.enums.ImageType;
import com.project.blog.global.exception.business.CustomException;
import com.project.blog.global.exception.enums.ExceptionType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class ImageController {

    private final ImageService imageService;

    // 이미지 업로드
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> S3Upload(
            @RequestPart(required = false) MultipartFile image,
            @RequestPart(required = false) String imageType
    ) {
        if (!ImageType.isValid(imageType)) {
            throw new CustomException(ExceptionType.IMAGE_TYPE_BAD_REQUEST);
        }

        ImageResponseDto profileImage = imageService.upload(image, imageType);

        ApiResponse result = ApiResponse.created(profileImage);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 이미지 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Void> S3Delete(
            @RequestBody @Valid ImageRequestDto dto
    ) {
        imageService.deleteImageFromS3(dto.getAddr());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
