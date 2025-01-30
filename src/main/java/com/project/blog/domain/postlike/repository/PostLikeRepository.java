package com.project.blog.domain.postlike.repository;

import com.project.blog.domain.post.entity.Post;
import com.project.blog.domain.postlike.entity.PostLike;
import com.project.blog.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostAndUser(Post post, User user);

    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    @Query("SELECT pl.post FROM PostLike pl WHERE pl.user.id = :userId")
    Page<Post> findLikedPostsByUser(Long userId, Pageable pageable);
}
