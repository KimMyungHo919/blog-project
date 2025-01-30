package com.project.blog.domain.comment.service;

import com.project.blog.domain.comment.dto.request.CommentRequestDto;
import com.project.blog.domain.comment.dto.response.CommentResponseDto;
import com.project.blog.domain.comment.entity.Comment;
import com.project.blog.domain.comment.repository.CommentRepository;
import com.project.blog.domain.post.entity.Post;
import com.project.blog.domain.post.repository.PostRepository;
import com.project.blog.domain.user.entity.User;
import com.project.blog.domain.user.repository.UserRepository;
import com.project.blog.global.exception.CustomException;
import com.project.blog.global.exception.ExceptionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    // 댓글생성
    @Transactional
    public CommentResponseDto createComment(Long postId, Long userId, CommentRequestDto dto) {
        // post 조회
        Post post = postRepository.findByIdOrElseThrow(postId);

        // user 조회
        User user = userRepository.findByIdOrElseThrow(userId);

        // comment 생성
        Comment comment = new Comment(dto.getComment());

        // 연관관계 추가
        comment.setPost(post);
        comment.setUser(user);

        // 데이터베이스 저장
        commentRepository.save(comment);

        return new CommentResponseDto(
                comment.getId(),
                comment.getComment(),
                user.getNickname(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findByIdWithUserOrElseThrow(commentId);

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw new CustomException(ExceptionType.USER_NOT_MATCH);
        }

        commentRepository.delete(comment);
    }
}
