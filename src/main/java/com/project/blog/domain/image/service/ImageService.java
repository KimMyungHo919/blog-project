package com.project.blog.domain.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.project.blog.domain.image.dto.ImageResponseDto;
import com.project.blog.domain.image.entity.Image;
import com.project.blog.domain.image.repository.ImageRepository;
import com.project.blog.global.enums.ImageType;
import com.project.blog.global.exception.business.CustomException;
import com.project.blog.global.exception.enums.ExceptionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ImageService {

    private final AmazonS3 amazonS3;
    private final ImageRepository imageRepository;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    // 이미지 업로드
    @Transactional
    public ImageResponseDto upload(MultipartFile image, String imageType) {
        //입력받은 이미지 파일이 빈 파일인지 검증
        if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new CustomException(ExceptionType.EMPTY_FILE_EXCEPTION);
        }
        String imageUrl = this.uploadImage(image);

        Image postImage = new Image(imageUrl, ImageType.from(imageType));
        imageRepository.save(postImage);
        //uploadImage 를 호출하여 S3에 저장된 이미지의 public url 을 반환한다.
        return new ImageResponseDto(postImage.getId(), imageUrl, imageType);
    }

    /*
    1. validateImageFileExtention()을 호출하여 확장자 명이 올바른지 확인한다.
    2. uploadImageToS3()를 호출하여 이미지를 S3에 업로드하고, S3에 저장된 이미지의 public url을 받아서 서비스 로직에 반환한다.
    */
    private String uploadImage(MultipartFile image) {
        this.validateImageFileExtention(image.getOriginalFilename());
        try {
            return this.uploadImageToS3(image);
        } catch (IOException e) {
            throw new CustomException(ExceptionType.ON_IMAGE_UPLOAD);
        }
    }

    // filename 을 받아서 파일 확장자가 jpg, jpeg, png, gif 중에 속하는지 검증한다.
    private void validateImageFileExtention(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new CustomException(ExceptionType.NO_FILE_EXTENTION);
        }

        String extention = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(extention)) {
            throw new CustomException(ExceptionType.NO_FILE_EXTENTION);
        }
    }

    // 직접적으로 S3에 업로드하는 메서드
    private String uploadImageToS3(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename(); //원본 파일 명
        String extention = originalFilename.substring(originalFilename.lastIndexOf(".")); //확장자 명

        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFilename; //변경된 파일 명

        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is); //image 를 byte[]로 변환

        ObjectMetadata metadata = new ObjectMetadata(); //metadata 생성
        metadata.setContentType("image/" + extention);
        metadata.setContentLength(bytes.length);

        //S3에 요청할 때 사용할 byteInputStream 생성
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            //S3로 putObject 할 때 사용할 요청 객체
            //생성자 : bucket 이름, 파일 명, byteInputStream, metadata
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);

            //실제로 S3에 이미지 데이터를 넣는 부분이다.
            amazonS3.putObject(putObjectRequest); // put image to S3
        } catch (Exception e) {
            throw new CustomException(ExceptionType.PUT_OBJECT_EXCEPTION);
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }

    /*
    1. 이미지의 public url 을 이용하여 S3에서 해당 이미지를 제거하는 메서드이다.
    2. getKeyFromImageAddress()를 호출하여 삭제에 필요한 key 를 얻는다.
    */
    @Transactional
    public void deleteImageFromS3(String imageAddress) {
        Image image = imageRepository.findByImgUrl(imageAddress)
                .orElseThrow(() -> new CustomException(ExceptionType.IMAGE_NOT_FOUND));
        String key = getKeyFromImageAddress(imageAddress);
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        } catch (Exception e) {
            throw new CustomException(ExceptionType.ON_IMAGE_DELETE);
        }
        imageRepository.delete(image);
    }

    private String getKeyFromImageAddress(String imageAddress) {
        try {
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1); // 맨 앞의 '/' 제거
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new CustomException(ExceptionType.ON_IMAGE_DELETE);
        }
    }
}
