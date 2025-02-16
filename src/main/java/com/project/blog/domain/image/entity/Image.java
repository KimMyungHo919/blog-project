package com.project.blog.domain.image.entity;

import com.project.blog.global.base.BaseTimeEntity;
import com.project.blog.global.enums.ImageType;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Image extends BaseTimeEntity {


    /**
     * 컬럼 - 연관관계 컬럼을 제외한 컬럼을 정의합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgUrl; // 이미지 url

    private String originalFileName; // 원본이미지 이름

    private Long fileSize; // 파일크기

    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    /**
     * 생성자 - 약속된 형태로만 생성가능하도록 합니다.
     */
    public Image() {}
    public Image(String imgUrl, String originalFileName, Long fileSize, ImageType imageType) {
        this.imgUrl = imgUrl;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
        this.imageType = imageType;
    }


    /**
     * 연관관계 - Foreign Key 값을 따로 컬럼으로 정의하지 않고 연관 관계로 정의합니다.
     */

    /**
     * 연관관계 편의 메소드 - 반대쪽에는 연관관계 편의 메소드가 없도록 주의합니다.
     */

    /**
     * 서비스 메소드 - 외부에서 엔티티를 수정할 메소드를 정의합니다. (단일 책임을 가지도록 주의합니다.)
     */

}
