package com.project.blog.domain.comment.repository;

import com.project.blog.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.user.id = :userId")
    Page<Comment> findAllCommentsWithUser(Long userId, Pageable pageable);
}
