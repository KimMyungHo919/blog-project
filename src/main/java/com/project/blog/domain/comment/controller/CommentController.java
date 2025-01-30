package com.project.blog.domain.comment.controller;

import com.project.blog.domain.comment.dto.request.CommentRequestDto;
import com.project.blog.domain.comment.dto.response.CommentResponseDto;
import com.project.blog.domain.comment.service.CommentService;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.constants.SessionAttributeKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
            @RequestBody CommentRequestDto dto,
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        CommentResponseDto result = commentService.createComment(postId, user.getId(), dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
