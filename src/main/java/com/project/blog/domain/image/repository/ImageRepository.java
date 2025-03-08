package com.project.blog.domain.image.repository;

import com.project.blog.domain.image.entity.Image;
import com.project.blog.global.enums.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByImgUrl(String imgUrl);

    List<Image> findByImageTypeIsNull();

    @Modifying
    @Query("UPDATE Image i " +
            "SET i.imageType = NULL " +
            "WHERE i.imgUrl IN :imageUrls")
    void updateTypeNullByImageUrl(List<String> imageUrls);

}
