package com.project.blog.domain.comment.service;

import com.project.blog.domain.comment.dto.request.CommentRequestDto;
import com.project.blog.domain.comment.dto.request.CommentUpdateRequestDto;
import com.project.blog.domain.comment.dto.response.CommentResponseDto;
import com.project.blog.domain.comment.entity.Comment;
import com.project.blog.domain.comment.repository.CommentRepository;
import com.project.blog.domain.post.entity.Post;
import com.project.blog.domain.post.repository.PostRepository;
import com.project.blog.domain.user.entity.User;
import com.project.blog.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    PostRepository postRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentService commentService;

    @Test
    @DisplayName("댓글생성")
    void createComment() {
        // given - 준비
        Long postId = 1L;
        Long userId = 100L;

        CommentRequestDto dto = new CommentRequestDto("댓글1");

        given(postRepository.findByIdOrElseThrow(postId)).willReturn(new Post());
        given(userRepository.findByIdOrElseThrow(userId)).willReturn(new User());

        // when - 실행
        CommentResponseDto result = commentService.createComment(postId, userId, dto);

        // then - 결과확인
        assertThat(result.getComment()).isEqualTo("댓글1");

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글수정")
    void updateComment() {
        // given - 준비
        Long commentId = 1L;
        Long userId = 100L;
        CommentUpdateRequestDto dto = new CommentUpdateRequestDto("수정댓글");

        Comment comment = new Comment("원래댓글");
        comment.setUser(new User(100L));
        comment.setPost(new Post());

        given(commentRepository.findByIdWithUserOrElseThrow(commentId)).willReturn(comment);

        // when - 실행
        commentService.updateComment(commentId, userId, dto);

        // then - 결과확인
        assertEquals(dto.getComment(), comment.getComment());
    }

    @Test
    @DisplayName("댓글삭제")
    void deleteComment() {
        // given - 준비
        Long commentId = 1L;
        Long userId = 100L;

        Comment comment = new Comment("댓글");
        comment.setUser(new User(userId));

        given(commentRepository.findByIdWithUserOrElseThrow(commentId)).willReturn(comment);
        willDoNothing().given(commentRepository).delete(comment); // 동작하지는 않게한다.

        // when - 실행
        commentService.deleteComment(commentId, userId);

        // then - 결과확인
        verify(commentRepository).delete(comment);
    }
}