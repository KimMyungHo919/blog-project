package com.project.blog.domain.s3.repository;

import com.project.blog.domain.s3.entity.S3Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface S3ImageRepository extends JpaRepository<S3Image, Long> {

    // 포스트 ID 와 해당 포스트의 이미지 URL 을 받아서 postID 를 업데이트 해주는 쿼리
    @Modifying
    @Query("UPDATE S3Image s SET s.post.id = :postId WHERE s.imgUrl IN :imageUrls")
    void updatePostIdByImgUrls(Long postId, List<String> imageUrls);

    List<S3Image> findByPostIdIsNull();
}
