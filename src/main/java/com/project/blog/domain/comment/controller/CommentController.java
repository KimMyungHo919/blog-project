package com.project.blog.domain.comment.controller;

import com.project.blog.domain.comment.dto.request.CommentRequestDto;
import com.project.blog.domain.comment.dto.request.CommentUpdateRequestDto;
import com.project.blog.domain.comment.dto.response.CommentResponseDto;
import com.project.blog.domain.comment.service.CommentService;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.base.ApiResponse;
import com.project.blog.global.constants.SessionAttributeKeys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "댓글 API", description = "댓글 관련 API")
public class CommentController {

    private final CommentService commentService;

    // 댓글생성
    @PostMapping("/post/{postId}")
    @Operation(summary = "댓글 생성", description = "포스팅에 댓글을 생성합니다.")
    public ResponseEntity<ApiResponse> createComment(
            @PathVariable Long postId,
            @RequestBody @Valid CommentRequestDto dto,
            HttpServletRequest request
    ) {
        Long userId = userIdFromRequest(request);

        CommentResponseDto comment = commentService.createComment(postId, userId, dto);

        ApiResponse result = ApiResponse.created(comment);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다.")
    public ResponseEntity<ApiResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateRequestDto dto,
            HttpServletRequest request
    ) {
        Long userId = userIdFromRequest(request);

        commentService.updateComment(commentId, userId, dto);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("댓글 수정완료"));
    }

    // 댓글삭제
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public ResponseEntity<ApiResponse> deleteComment(
            @PathVariable Long commentId,
            HttpServletRequest request
    ) {
        Long userId = userIdFromRequest(request);

        commentService.deleteComment(commentId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("댓글 삭제완료"));
    }

    // 유저아이디 추출
    private Long userIdFromRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        return user.getId();
    }
}
