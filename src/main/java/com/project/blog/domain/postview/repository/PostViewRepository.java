package com.project.blog.domain.postview.repository;

import com.project.blog.domain.postview.entity.PostView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PostViewRepository extends JpaRepository<PostView, Long> {

    @Query("SELECT pv FROM PostView pv WHERE pv.createdAt < :beforeDate")
    List<PostView> findBeforeDate(LocalDateTime beforeDate, Pageable pageable);

    boolean existsByUserIdAndPostId(Long id, Long postId);
}
