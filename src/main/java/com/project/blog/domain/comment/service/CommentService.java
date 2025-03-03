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
import com.project.blog.global.exception.business.CustomException;
import com.project.blog.global.exception.enums.ExceptionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 댓글(Comment)과 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    /**
     * 새로운 댓글을 생성합니다.
     *
     * @param postId 댓글을 추가할 게시글 ID
     * @param userId 댓글 작성자의 사용자 ID
     * @param dto    댓글 생성 요청 DTO
     * @return 생성된 댓글 정보를 담은 {@code CommentResponseDto}
     * @throws CustomException 게시글 또는 사용자 정보가 존재하지 않을 경우 발생
     */
    @Transactional
    public CommentResponseDto createComment(Long postId, Long userId, CommentRequestDto dto) {
        Post post = postRepository.findByIdOrElseThrow(postId);
        User user = userRepository.findByIdOrElseThrow(userId);

        Comment comment = new Comment(dto.getComment());
        comment.setPost(post);
        comment.setUser(user);

        commentRepository.save(comment);

        return new CommentResponseDto(
                comment.getId(),
                comment.getComment(),
                user.getNickname(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }

    /**
     * 댓글을 수정합니다.
     *
     * @param commentId 수정할 댓글의 ID
     * @param userId    댓글 작성자의 사용자 ID
     * @param dto       댓글 수정 요청 DTO
     * @throws CustomException 사용자가 댓글 작성자가 아닐 경우 발생
     */
    @Transactional
    public void updateComment(Long commentId, Long userId, CommentUpdateRequestDto dto) {
        Comment comment = commentRepository.findByIdWithUserOrElseThrow(commentId);

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw new CustomException(ExceptionType.USER_NOT_MATCH);
        }

        comment.updateComment(dto.getComment());
    }

    /**
     * 댓글을 삭제합니다.
     *
     * @param commentId 삭제할 댓글의 ID
     * @param userId    댓글 작성자의 사용자 ID
     * @throws CustomException 사용자가 댓글 작성자가 아닐 경우 발생
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findByIdWithUserOrElseThrow(commentId);

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw new CustomException(ExceptionType.USER_NOT_MATCH);
        }

        commentRepository.delete(comment);
    }

}
