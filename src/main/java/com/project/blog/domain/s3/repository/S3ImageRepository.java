package com.project.blog.domain.s3.repository;

import com.project.blog.domain.s3.entity.S3Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface S3ImageRepository extends JpaRepository<S3Image, Long> {

}
