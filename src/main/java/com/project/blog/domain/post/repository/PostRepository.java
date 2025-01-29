package com.project.blog.domain.post.repository;

import com.project.blog.domain.post.entity.Post;
import com.project.blog.global.exception.CustomException;
import com.project.blog.global.exception.ExceptionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    default Post findByIdOrElseThrow(Long postId) {
        return findById(postId).orElseThrow(() -> new CustomException(ExceptionType.POST_NOT_FOUND));
    }

    @Query("SELECT p FROM Post p JOIN FETCH p.user")
    Page<Post> findAllPostsWithUser(Pageable pageable);

}
