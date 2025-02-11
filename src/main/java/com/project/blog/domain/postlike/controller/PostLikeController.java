package com.project.blog.domain.postlike.controller;

import com.project.blog.domain.postlike.service.PostLikeService;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.base.ApiResponse;
import com.project.blog.global.constants.SessionAttributeKeys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
@Tag(name = "좋아요 API", description = "좋아요 관련 API")
public class PostLikeController {

    private final PostLikeService postLikeService;

    // 좋아요 누르기
    @PostMapping("/post/{postId}")
    @Operation(summary = "좋아요 누르기", description = "포스팅에 좋아요를 누릅니다.")
    public ResponseEntity<ApiResponse> addPostLike(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        Long userId = this.userIdFromRequest(request);

        postLikeService.addPostLike(postId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("좋아요 완료"));
    }

    // 좋아요 취소
    @DeleteMapping("/post/{postId}")
    @Operation(summary = "좋아요 취소", description = "좋아요를 취소합니다.")
    public ResponseEntity<ApiResponse> cancelPostLike(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        Long userId = this.userIdFromRequest(request);

        postLikeService.cancelPostLike(postId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("좋아요 취소완료"));
    }

    // request 에서 유저아이디 추출
    private Long userIdFromRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        return user.getId();
    }
}
