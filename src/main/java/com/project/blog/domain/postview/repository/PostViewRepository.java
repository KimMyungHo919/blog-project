package com.project.blog.domain.postview.repository;

import com.project.blog.domain.post.entity.Post;
import com.project.blog.domain.postview.entity.PostView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PostViewRepository extends JpaRepository<PostView, Long> {

    @Query("SELECT pv FROM PostView pv WHERE pv.createdAt < :beforeDate")
    List<PostView> findBeforeDate(LocalDateTime beforeDate, Pageable pageable);

    boolean existsByUserIdAndPostId(Long id, Long postId);

    @Query("SELECT pv.post " +
            "FROM PostView pv " +
            "WHERE pv.user.id = :loginUserId")
    Page<Post> findByUserIdRecentView(Long loginUserId, Pageable pageable);
}
