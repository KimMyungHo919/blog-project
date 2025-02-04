package com.project.blog.domain.post.repository;

import com.project.blog.domain.post.entity.Post;
import com.project.blog.global.exception.CustomException;
import com.project.blog.global.exception.ExceptionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    default Post findByIdOrElseThrow(Long postId) {
        return findById(postId).orElseThrow(() -> new CustomException(ExceptionType.POST_NOT_FOUND));
    }

    default Post findByPostWithUserOrElseThrow(Long postId) {
        return findByPostIdWithUser(postId).orElseThrow(() -> new CustomException(ExceptionType.POST_NOT_FOUND));
    }

    // TODO : 쿼리 최적화 필요함. yml Batch Size 로 해결.
    @Query("SELECT p FROM Post p JOIN FETCH p.user")
    Page<Post> findAllPosts(Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.id = :postId")
    Optional<Post> findByPostIdWithUser(Long postId);

    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.user.id = :userId")
    Page<Post> findAllPostsWithUser(Long userId, Pageable pageable);

}
