package com.project.blog.domain.post.controller;

import com.project.blog.domain.post.dto.request.PostRequestDto;
import com.project.blog.domain.post.dto.request.PostUpdateRequestDto;
import com.project.blog.domain.post.dto.response.PostCommentsResponseDto;
import com.project.blog.domain.post.dto.response.PostResponseDto;
import com.project.blog.domain.post.service.PostService;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.constants.SessionAttributeKeys;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    // 포스팅작성
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @Valid @RequestBody PostRequestDto dto,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        PostResponseDto result = postService.createPost(user.getId(), dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 글 조회 -> 하나의 포스팅만 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> findPost(
            @PathVariable Long postId
    ) {
        PostResponseDto result = postService.findPost(postId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 글 조회 -> 전체포스팅 조회
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> findAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.status(HttpStatus.OK).body(postService.findAllPosts(pageable));
    }

    // 글 업데이트
    @PatchMapping("/{postId}")
    public ResponseEntity<String> updatePost(
            @PathVariable Long postId,
            @RequestBody PostUpdateRequestDto dto,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        postService.updatePost(user.getId(), postId, dto);

        return ResponseEntity.status(HttpStatus.OK).body("글 업데이트 완료");
    }

    // 글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        postService.deletePost(user.getId(), postId);

        return ResponseEntity.status(HttpStatus.OK).body("삭제가 완료되었습니다.");
    }

    // 한 포스팅의 댓글 전체조회
    @GetMapping("/{postId}/comments")
    public ResponseEntity<Page<PostCommentsResponseDto>> findAllCommentsOfPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PostCommentsResponseDto> result = postService.findAllCommentsOfPost(postId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
