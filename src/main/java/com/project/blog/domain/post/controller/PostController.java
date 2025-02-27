package com.project.blog.domain.post.controller;

import com.project.blog.domain.post.dto.request.PostPageRequestParams;
import com.project.blog.domain.post.dto.request.PostRequestDto;
import com.project.blog.domain.post.dto.request.PostUpdateRequestDto;
import com.project.blog.domain.post.dto.response.PostCommentsResponseDto;
import com.project.blog.domain.post.dto.response.PostLikesUserResponseDto;
import com.project.blog.domain.post.dto.response.PostResponseDto;
import com.project.blog.domain.post.entity.Post;
import com.project.blog.domain.post.service.PostService;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.base.ApiResponse;
import com.project.blog.global.constants.SessionAttributeKeys;
import com.project.blog.global.exception.business.CustomException;
import com.project.blog.global.exception.enums.ExceptionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "포스팅 API", description = "포스팅 관련 API")
public class PostController {

    private final PostService postService;

    // 포스팅작성
    @PostMapping("/posts")
    @Operation(summary = "포스팅 작성", description = "새로운 포스팅을 추가합니다.")
    public ResponseEntity<ApiResponse> createPost(
            @RequestBody @Valid PostRequestDto dto,
            HttpServletRequest request
    ) {
        Long userId = this.userIdFromRequest(request);

        PostResponseDto post = postService.createPost(userId, dto);

        ApiResponse result = ApiResponse.created(post);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 글 조회 -> 하나의 포스팅만 조회
    @GetMapping("/public/posts/{postId}")
    @Operation(summary = "포스팅 조회", description = "ID로 하나의 포스팅을 조회합니다.")
    public ResponseEntity<ApiResponse> findPost(
            @PathVariable Long postId,
            HttpServletRequest request
    ) throws InterruptedException {
        Long userId = this.userIdFromRequest(request);

        PostResponseDto post = postService.findPost(postId, userId);

        ApiResponse result = ApiResponse.success(post);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 글 조회 -> 전체 공개 포스팅 조회
    @GetMapping("/public/posts")
    @Operation(summary = "전체 포스팅 조회", description = "공개상태인 전체 포스팅을 조회합니다.")
    public ResponseEntity<ApiResponse> findAllPosts(@Validated PostPageRequestParams params) {
        Sort sort = params.getDirection().equalsIgnoreCase("desc") ?
                Sort.by(params.getSortBy()).descending() : Sort.by(params.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(params.getPage(), params.getSize(), sort);

        Page<PostResponseDto> postResponseDto = postService.findAllPosts(pageable);

        ApiResponse result = ApiResponse.success(postResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 나의 비밀글 조회
    @GetMapping("/secret/posts")
    @Operation(summary = "비공개 글 조회", description = "로그인 유저의 비밀글을 조회합니다.")
    public ResponseEntity<ApiResponse> findMyPrivatePost(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        Pageable pageable = this.pagingValidation(page, size);

        Long userId = this.userIdFromRequest(request);

        Page<PostResponseDto> postResponseDto = postService.findMyPrivatePost(userId, pageable);

        ApiResponse result = ApiResponse.success(postResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 나의 임시저장글 조회
    @GetMapping("/draft/posts")
    @Operation(summary = "임시저장 글 조회", description = "로그인 유저의 임시저장글을 조회합니다.")
    public ResponseEntity<ApiResponse> findMyDraftPost(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        Pageable pageable = this.pagingValidation(page, size);

        Long userId = this.userIdFromRequest(request);

        Page<PostResponseDto> postResponseDto = postService.findMyDraftPost(userId, pageable);

        ApiResponse result = ApiResponse.success(postResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 글 업데이트
    @PatchMapping("/posts/{postId}")
    @Operation(summary = "포스팅 수정", description = "포스팅을 수정합니다.")
    public ResponseEntity<ApiResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid PostUpdateRequestDto dto,
            HttpServletRequest request
    ) {
        Long userId = this.userIdFromRequest(request);

        postService.updatePost(userId, postId, dto);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("포스팅 업데이트완료"));
    }

    // 글 삭제
    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "포스팅 삭제", description = "포스팅을 삭제합니다.")
    public ResponseEntity<ApiResponse> deletePost(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        Long userId = this.userIdFromRequest(request);

        postService.deletePost(userId, postId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("포스팅 삭제완료"));
    }

    // 한 포스팅의 댓글 전체조회
    @GetMapping("/public/posts/{postId}/comments")
    @Operation(summary = "포스팅 댓글 조회", description = "포스팅의 전체댓글을 조회합니다.")
    public ResponseEntity<ApiResponse> findAllCommentsOfPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        Pageable pageable = this.pagingValidation(page, size);

        Long userId = this.userIdFromRequest(request);

        Page<PostCommentsResponseDto> post = postService.findAllCommentsOfPost(postId, userId, pageable);

        ApiResponse result = ApiResponse.success(post);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 한 포스팅의 좋아요 누른 유저의 정보 조회
    @GetMapping("/public/posts/{postId}/likes")
    @Operation(summary = "좋아요 누른 유저조회", description = "포스팅에 좋아요를 누른 유저닉네임을 조회합니다.")
    public ResponseEntity<ApiResponse> findAllLikesUserData(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        Pageable pageable = this.pagingValidation(page, size);

        Long userId = this.userIdFromRequest(request);

        Page<PostLikesUserResponseDto> postLikesUserResponseDto = postService.findAllLikesUserData(postId, userId, pageable);

        ApiResponse result = ApiResponse.success(postLikesUserResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 포스팅 타이틀로 검색기능
    @GetMapping("/public/posts/search")
    public ResponseEntity<ApiResponse> searchTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = this.pagingValidation(page, size);

        Page<PostResponseDto> postResponseDto = postService.searchTitle(title, pageable);

        ApiResponse result = ApiResponse.success(postResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 최신인기글 10개 조회
    @GetMapping("/public/posts/top-ten")
    public ResponseEntity<ApiResponse> topTenPosts() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<PostResponseDto> posts = postService.topTenPosts(pageable);

        ApiResponse result = ApiResponse.success(posts);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 페이징 유효성 검사
    private Pageable pagingValidation(int page, int size) {
        if (page < 0) {
            throw new CustomException(ExceptionType.PAGE_BAD_REQUEST);
        }
        if (size < 1 || size > 20) {
            throw new CustomException(ExceptionType.PAGE_SIZE_BAD_REQUEST);
        }

        return PageRequest.of(page, size);
    }

    // 유저 아이디 추출
    private Long userIdFromRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute(SessionAttributeKeys.USER) : null;

        return (user != null) ? user.getId() : null;
    }
}
