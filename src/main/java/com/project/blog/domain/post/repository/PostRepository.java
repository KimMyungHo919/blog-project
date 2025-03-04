package com.project.blog.domain.post.repository;

import com.project.blog.domain.post.entity.Post;
import com.project.blog.global.enums.PostCategory;
import com.project.blog.global.exception.business.CustomException;
import com.project.blog.global.exception.enums.ExceptionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    default Post findByIdOrElseThrow(Long postId) {
        return findById(postId).orElseThrow(() -> new CustomException(ExceptionType.POST_NOT_FOUND));
    }

    default Post findByPostWithUserOrElseThrow(Long postId) {
        return findByPostIdWithUser(postId).orElseThrow(() -> new CustomException(ExceptionType.POST_NOT_FOUND));
    }

    @Query("SELECT p " +
            "FROM Post p " +
            "JOIN FETCH p.user " +
            "WHERE p.postVisibility = 'PUBLIC'")
    Page<Post> findAllPosts(Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "JOIN FETCH p.user " +
            "WHERE p.id = :postId")
    Optional<Post> findByPostIdWithUser(Long postId);

    @Query("SELECT p " +
            "FROM Post p " +
            "JOIN FETCH p.user " +
            "WHERE p.user.id = :userId " +
            "AND p.postVisibility = 'PUBLIC'")
    Page<Post> findAllPostsWithUser(Long userId, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "JOIN FETCH p.user " +
            "WHERE p.user.id = :loginUserId " +
            "AND p.postVisibility = 'PRIVATE'" +
            "ORDER BY p.createdAt DESC")
    Page<Post> findMyPrivatePost(Long loginUserId, Pageable pageable);

    Optional<Post> findByIdAndUserId(Long postId, Long userId);

    @Query("SELECT p " +
            "FROM Post p " +
            "JOIN FETCH p.user " +
            "WHERE p.title LIKE %:title% " +
            "AND p.postVisibility = 'PUBLIC' " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findByTitlePage(String title, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.postVisibility = 'PUBLIC' AND p.user.id IN (" +
            "SELECT f.sender.id FROM Friend f WHERE f.receiver.id = :loginUserId AND f.friendStatus = 'ACCEPTED' " +
            "UNION " +
            "SELECT f.receiver.id FROM Friend f WHERE f.sender.id = :loginUserId AND f.friendStatus = 'ACCEPTED')")
    Page<Post> findMyFriendPosts(Long loginUserId, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "JOIN FETCH p.user " +
            "WHERE p.user.id = :loginUserId " +
            "AND p.postVisibility = 'DRAFT'" +
            "ORDER BY p.createdAt DESC")
    Page<Post> findMyDraftPost(Long loginUserId, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "WHERE p.createdAt >= :oneWeekAgo " +
            "AND p.postVisibility = 'PUBLIC' " +
            "ORDER BY p.views DESC")
    Page<Post> findTopTenPosts(LocalDateTime oneWeekAgo, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.user " +
            "WHERE p.postCategory = :postCategory " +
            "AND p.postVisibility = 'PUBLIC'")
    Page<Post> findByCategoryPage(PostCategory postCategory, Pageable pageable);
}
