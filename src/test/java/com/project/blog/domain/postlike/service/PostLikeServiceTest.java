package com.project.blog.domain.postlike.service;

import com.project.blog.domain.post.entity.Post;
import com.project.blog.domain.post.repository.PostRepository;
import com.project.blog.domain.postlike.entity.PostLike;
import com.project.blog.domain.postlike.repository.PostLikeRepository;
import com.project.blog.domain.user.entity.User;
import com.project.blog.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {

    @Mock
    PostRepository postRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    PostLikeRepository postLikeRepository;

    @InjectMocks
    PostLikeService postLikeService;

    @Test
    @DisplayName("좋아요 누름")
    void addPostLike() {
        // given
        Long postId = 1L;
        Long userId = 2L;

        Post post = new Post();
        User user = new User();

        given(postRepository.findByIdOrElseThrow(postId)).willReturn(post);
        given(userRepository.findByIdOrElseThrow(userId)).willReturn(user);
        given(postLikeRepository.existsByPostAndUser(post, user)).willReturn(false);

        PostLike postLike = new PostLike();
        postLike.setUser(user);
        postLike.setPost(post);

        given(postLikeRepository.save(any(PostLike.class))).willReturn(postLike);

        // when
        postLikeService.addPostLike(postId, userId);

        // then
        verify(postLikeRepository, times(1)).save(any(PostLike.class));
    }

    @Test
    @DisplayName("좋아요 취소")
    void cancelPostLike() {
        Long postId = 1L;
        Long userId = 2L;

        given(postRepository.existsById(postId)).willReturn(true);

        PostLike postLike = new PostLike();

        given(postLikeRepository.findByPostIdAndUserId(postId, userId)).willReturn(Optional.of(postLike));
        willDoNothing().given(postLikeRepository).delete(postLike);

        postLikeService.cancelPostLike(postId, userId);

        verify(postLikeRepository, times(1)).delete(postLike);
    }
}