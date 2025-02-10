package com.project.blog.domain.postlike.controller;

import com.project.blog.domain.postlike.service.PostLikeService;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.base.ApiResponse;
import com.project.blog.global.constants.SessionAttributeKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class PostLikeController {

    private final PostLikeService postLikeService;

    // 좋아요 누르기
    @PostMapping("/post/{postId}")
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
