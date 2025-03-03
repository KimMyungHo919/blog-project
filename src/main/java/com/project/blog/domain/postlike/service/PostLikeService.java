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

/**
 * 게시글 좋아요 기능을 제공하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;

    /**
     * 게시글에 좋아요를 추가합니다.
     *
     * @param postId 좋아요를 추가할 게시글의 ID
     * @param userId 좋아요를 누른 사용자의 ID
     * @throws CustomException 게시글이 존재하지 않거나 이미 좋아요를 눌렀을 경우 예외 발생
     */
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

    /**
     * 게시글 좋아요를 취소합니다.
     *
     * @param postId 좋아요를 취소할 게시글의 ID
     * @param userId 좋아요를 취소할 사용자의 ID
     * @throws CustomException 게시글이 존재하지 않거나 해당 사용자가 좋아요를 누르지 않았을 경우 예외 발생
     */
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
