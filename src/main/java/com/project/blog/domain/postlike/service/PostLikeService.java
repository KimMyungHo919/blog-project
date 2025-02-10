package com.project.blog.domain.postlike.service;

import com.project.blog.domain.post.entity.Post;
import com.project.blog.domain.post.repository.PostRepository;
import com.project.blog.domain.postlike.entity.PostLike;
import com.project.blog.domain.postlike.repository.PostLikeRepository;
import com.project.blog.domain.user.entity.User;
import com.project.blog.domain.user.repository.UserRepository;
import com.project.blog.global.exception.business.CustomException;
import com.project.blog.global.exception.enums.ExceptionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;

    // 좋아요 누름
    @Transactional
    public void addPostLike(Long postId, Long userId) {
        Post post = postRepository.findByIdOrElseThrow(postId);

        User user = userRepository.findByIdOrElseThrow(userId);

        if (postLikeRepository.existsByPostAndUser(post, user)) {
            throw new CustomException(ExceptionType.ALREADY_POST_LIKE);
        }

        PostLike postLike = new PostLike();
        postLike.setPost(post);
        postLike.setUser(user);

        postLikeRepository.save(postLike);
    }

    // 좋아요 취소
    @Transactional
    public void cancelPostLike(Long postId, Long userId) {
        if (!postRepository.existsById(postId)) {
            throw new CustomException(ExceptionType.POST_NOT_FOUND);
        }

        PostLike postLike = postLikeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new CustomException(ExceptionType.NOTFOUND_POST_LIKE));

        postLikeRepository.delete(postLike);
    }
}
