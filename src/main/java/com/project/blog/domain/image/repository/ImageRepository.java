package com.project.blog.domain.image.repository;

import com.project.blog.domain.image.entity.Image;
import com.project.blog.global.enums.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    // 포스트 ID 와 해당 포스트의 이미지 URL 을 받아서 postID 를 업데이트 해주는 쿼리
    @Modifying
    @Query("UPDATE Image i " +
            "SET i.imageType = :imageType " +
            "WHERE i.imgUrl IN :imageUrls")
    void updatePostTypeByImgUrls(List<String> imageUrls, ImageType imageType);

    @Modifying
    @Query("UPDATE Image i " +
            "SET i.imageType = :imageType " +
            "WHERE i.imgUrl = :profileImage")
    void updateUserTypeByImgUrls(String profileImage, ImageType imageType);

    List<Image> findByImageTypeIsNull();

    @Modifying
    @Query("UPDATE Image i " +
            "SET i.imageType = NULL " +
            "WHERE i.imgUrl IN :imageUrls")
    void updateTypeNullByImageUrl(List<String> imageUrls);

    Optional<Image> findByImgUrl(String imgUrl);

}
