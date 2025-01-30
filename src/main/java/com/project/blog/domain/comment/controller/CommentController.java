package com.project.blog.domain.comment.controller;

import com.project.blog.domain.comment.dto.request.CommentRequestDto;
import com.project.blog.domain.comment.dto.request.CommentUpdateRequestDto;
import com.project.blog.domain.comment.dto.response.CommentResponseDto;
import com.project.blog.domain.comment.service.CommentService;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.constants.SessionAttributeKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글생성
    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long postId,
            @RequestBody @Valid CommentRequestDto dto,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        CommentResponseDto result = commentService.createComment(postId, user.getId(), dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<String> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateRequestDto dto,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        commentService.updateComment(commentId, user.getId(), dto);

        return ResponseEntity.status(HttpStatus.OK).body("댓글 수정 완료.");
    }

    // 댓글삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long commentId,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        commentService.deleteComment(commentId, user.getId());

        return ResponseEntity.status(HttpStatus.OK).body("댓글 삭제 완료.");
    }
}
