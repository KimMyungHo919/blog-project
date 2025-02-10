package com.project.blog.domain.comment.repository;

import com.project.blog.domain.comment.entity.Comment;
import com.project.blog.global.exception.business.CustomException;
import com.project.blog.global.exception.enums.ExceptionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    default Comment findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() -> new CustomException(ExceptionType.COMMENT_NOT_FOUND));
    }

    default Comment findByIdWithUserOrElseThrow(Long commentId) {
        return findByIdWithUser(commentId).orElseThrow(() -> new CustomException(ExceptionType.COMMENT_NOT_FOUND));
    }

    @Query("SELECT c " +
            "FROM Comment c " +
            "JOIN FETCH c.user " +
            "WHERE c.id = :commentId")
    Optional<Comment> findByIdWithUser(Long commentId);

    @Query("SELECT c " +
            "FROM Comment c " +
            "JOIN FETCH c.user " +
            "WHERE c.user.id = :userId")
    Page<Comment> findAllCommentsWithUser(Long userId, Pageable pageable);

    @Query("SELECT c " +
            "FROM Comment c " +
            "JOIN FETCH c.post " +
            "JOIN FETCH c.user " +
            "WHERE c.post.id = :postId " +
            "ORDER BY c.createdAt ASC")
    Page<Comment> findAllCommentsWithPost(Long postId, Pageable pageable);
}
