package com.project.blog.domain.post.controller;

import com.project.blog.domain.post.dto.request.PostRequestDto;
import com.project.blog.domain.post.dto.response.PostResponseDto;
import com.project.blog.domain.post.repository.PostRepository;
import com.project.blog.domain.post.service.PostService;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.constants.SessionAttributeKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
    private final PostRepository postRepository;

    // 포스팅작성
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @RequestBody PostRequestDto dto,
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

    // u

    // d
}
