package com.project.blog.domain.post.controller;

import com.project.blog.domain.post.dto.request.PostPageRequestParams;
import com.project.blog.domain.post.dto.request.PostRequestDto;
import com.project.blog.domain.post.dto.request.PostUpdateRequestDto;
import com.project.blog.domain.post.dto.response.PostCommentsResponseDto;
import com.project.blog.domain.post.dto.response.PostLikesUserResponseDto;
import com.project.blog.domain.post.dto.response.PostResponseDto;
import com.project.blog.domain.post.service.PostService;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.base.ApiResponse;
import com.project.blog.global.base.DatePageRequestParams;
import com.project.blog.global.constants.SessionAttributeKeys;
import com.project.blog.global.exception.CustomException;
import com.project.blog.global.exception.ExceptionType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    // 포스팅작성
    @PostMapping("/posts")
    public ResponseEntity<ApiResponse> createPost(
            @Valid @RequestBody PostRequestDto dto,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        PostResponseDto post = postService.createPost(user.getId(), dto);

        ApiResponse result = ApiResponse.created(post);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 글 조회 -> 하나의 포스팅만 조회
    @GetMapping("/public/posts/{postId}")
    public ResponseEntity<ApiResponse> findPost(
            @PathVariable Long postId
    ) {
        PostResponseDto post = postService.findPost(postId);

        ApiResponse result = ApiResponse.success(post);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 글 조회 -> 전체포스팅 조회
    @GetMapping("/public/posts")
    public ResponseEntity<ApiResponse> findAllPosts(@Validated PostPageRequestParams params) {
        Sort sort = params.getDirection().equalsIgnoreCase("desc") ?
                Sort.by(params.getSortBy()).descending() : Sort.by(params.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(params.getPage(), params.getSize(), sort);

        Page<PostResponseDto> postResponseDto = postService.findAllPosts(pageable);

        ApiResponse result = ApiResponse.success(postResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 글 업데이트
    @PatchMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid PostUpdateRequestDto dto,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        postService.updatePost(user.getId(), postId, dto);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("포스팅 업데이트완료"));
    }

    // 글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse> deletePost(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        postService.deletePost(user.getId(), postId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("포스팅 삭제완료"));
    }

    // 한 포스팅의 댓글 전체조회
    @GetMapping("/public/posts/{postId}/comments")
    public ResponseEntity<ApiResponse> findAllCommentsOfPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 유효성 검사
        if (page < 0) {
            throw new CustomException(ExceptionType.PAGE_BAD_REQUEST);
        }
        if (size < 1 || size > 20) {
            throw new CustomException(ExceptionType.PAGE_SIZE_BAD_REQUEST);
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<PostCommentsResponseDto> post = postService.findAllCommentsOfPost(postId, pageable);

        ApiResponse result = ApiResponse.success(post);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 한 포스팅의 좋아요 누른 유저의 정보 조회
    @GetMapping("/public/posts/{postId}/likes")
    public ResponseEntity<ApiResponse> findAllLikesUserData(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 유효성 검사
        if (page < 0) {
            throw new CustomException(ExceptionType.PAGE_BAD_REQUEST);
        }
        if (size < 1 || size > 20) {
            throw new CustomException(ExceptionType.PAGE_SIZE_BAD_REQUEST);
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<PostLikesUserResponseDto> postLikesUserResponseDto = postService.findAllLikesUserData(postId, pageable);

        ApiResponse result = ApiResponse.success(postLikesUserResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
