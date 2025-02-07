package com.project.blog.domain.user.controller;

import com.project.blog.global.base.DatePageRequestParams;
import com.project.blog.domain.post.dto.request.PostPageRequestParams;
import com.project.blog.domain.user.dto.request.*;
import com.project.blog.domain.user.dto.response.*;
import com.project.blog.domain.user.entity.User;
import com.project.blog.domain.user.service.UserService;
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
public class UserController {

    private final UserService userService;

    // Create - Post
    // 회원가입
    @PostMapping("/public/users/signup")
    public ResponseEntity<UserSignupResponseDto> signupUser(
            @RequestBody @Valid UserSignupRequestDto dto
    ) {
        UserSignupResponseDto result = userService.signupUser(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 로그인
    @PostMapping("/public/users/login")
    public ResponseEntity<UserLoginResponseDto> loginUser(
            @RequestBody @Valid UserLoginRequestDto dto,
            HttpServletRequest request
    ) {
        // 기존세션 가져오기. 첫로그인이면 null
        HttpSession session = request.getSession(false);

        // 이미 로그인되어있는지 확인.
        if (session != null && session.getAttribute(SessionAttributeKeys.USER) != null) {
            throw new CustomException(ExceptionType.ALREADY_LOGIN);
        }

        // userService loginUser() 호출
        User user = userService.loginUser(dto);

        // 세션저장하기
        session = request.getSession(true);
        session.setAttribute(SessionAttributeKeys.USER, user);

        UserLoginResponseDto result = new UserLoginResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole()
        );

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/users/logout")
    public ResponseEntity<String> logout(
            HttpServletRequest request
    ) {
        request.getSession(false).invalidate();

        return ResponseEntity.status(HttpStatus.OK).body("로그아웃되었습니다.");
    }

    // Read - Get
    // 유저정보조회
    @GetMapping("/public/users/{userId}")
    public ResponseEntity<UserInfoResponseDto> getUserById(
            @PathVariable Long userId
    ) {
        UserInfoResponseDto result = userService.getUserById(userId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 한 유저의 포스팅들 조회
    @GetMapping("/public/users/{userId}/posts")
    public ResponseEntity<Page<UserPostsResponseDto>> findPostsByUser(
            @PathVariable Long userId,
            @Validated PostPageRequestParams params
    ) {
        Sort sort = params.getDirection().equalsIgnoreCase("desc") ?
                Sort.by(params.getSortBy()).descending() : Sort.by(params.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(params.getPage(), params.getSize(), sort);

        Page<UserPostsResponseDto> result = userService.findPostsByUser(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 한 유저의 댓글들 조회
    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<Page<UserCommentResponseDto>> findCommentsByUser(
            @PathVariable Long userId,
            @Validated DatePageRequestParams params
    ) {
        Sort sort = params.getDirection().equalsIgnoreCase("desc") ?
                Sort.by(params.getSortBy()).descending() : Sort.by(params.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(params.getPage(), params.getSize(), sort);

        Page<UserCommentResponseDto> result = userService.findCommentsByUser(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 한 유저의 좋아요 누른 게시물 조회
    @GetMapping("/users/{userId}/likes")
    public ResponseEntity<Page<UserPostLikeResponseDto>> findAllPostLike(
            @PathVariable Long userId,
            @Validated DatePageRequestParams params
    ) {
        Sort sort = params.getDirection().equalsIgnoreCase("desc") ?
                Sort.by(params.getSortBy()).descending() : Sort.by(params.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(params.getPage(), params.getSize(), sort);

        Page<UserPostLikeResponseDto> result = userService.findAllPostLike(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 한 유저의 친구목록 조회
    @GetMapping("/users/friends")
    public ResponseEntity<Page<UserFriendsResponseDto>> findMyFriends(
            @Validated DatePageRequestParams params,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        Pageable pageable = PageRequest.of(params.getPage(), params.getSize());

        Page<UserFriendsResponseDto> result = userService.findMyFriends(user.getId(), pageable);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Update - Patch,Put
    // 비밀번호변경
    @PatchMapping("/users/me/password")
    public ResponseEntity<String> changePassword(
            @RequestBody @Valid UserChangePasswordDto dto,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User loginUser = (User) session.getAttribute(SessionAttributeKeys.USER);

        userService.changePassword(loginUser.getId(), dto);

        session.invalidate();

        return ResponseEntity.status(HttpStatus.OK).body("비밀번호 변경 완료. 다시 로그인해주세요");
    }

    @PatchMapping("/users/me/nickname")
    public ResponseEntity<String> updateUserNickname(
            @RequestBody @Valid UserChangeNicknameDto dto,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        userService.updateUserNickname(user.getId(), dto);

        return ResponseEntity.status(HttpStatus.OK).body("닉네임 변경완료");
    }

    // Delete - Delete
    // 유저삭제, 탈퇴
    @DeleteMapping("/users/me")
    public ResponseEntity<String> deleteUser(
            @RequestBody @Valid UserDeleteRequestDto dto,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User loginUser = (User) session.getAttribute(SessionAttributeKeys.USER);

        userService.deleteUser(loginUser.getId(), dto);

        session.invalidate();

        return ResponseEntity.status(HttpStatus.OK).body("회원탈퇴가 완료되었습니다.");
    }
}
