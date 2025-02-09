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
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        postLikeService.addPostLike(postId, user.getId());

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("좋아요 완료"));
    }

    // 좋아요 취소
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<ApiResponse> cancelPostLike(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        postLikeService.cancelPostLike(postId, user.getId());

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("좋아요 취소완료"));
    }
}
