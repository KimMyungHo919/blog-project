package com.project.blog.domain.user.controller;

import com.project.blog.global.base.ApiResponse;
import com.project.blog.global.base.DatePageRequestParams;
import com.project.blog.domain.post.dto.request.PostPageRequestParams;
import com.project.blog.domain.user.dto.request.*;
import com.project.blog.domain.user.dto.response.*;
import com.project.blog.domain.user.entity.User;
import com.project.blog.domain.user.service.UserService;
import com.project.blog.global.constants.SessionAttributeKeys;
import com.project.blog.global.exception.business.CustomException;
import com.project.blog.global.exception.enums.ExceptionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
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
@Tag(name = "유저 API", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;

    // Create - Post
    // 회원가입
    @PostMapping("/public/users/signup")
    @Operation(summary = "회원가입", description = "새로운 유저를 추가합니다.")
    public ResponseEntity<ApiResponse> signupUser(
            @RequestBody @Valid UserSignupRequestDto dto
    ) throws MessagingException  {
        UserSignupResponseDto user = userService.signupUser(dto);

        ApiResponse result = ApiResponse.created(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 로그인
    @PostMapping("/public/users/login")
    @Operation(summary = "로그인", description = "로그인을 합니다.")
    public ResponseEntity<ApiResponse> loginUser(
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

        UserLoginResponseDto userLoginResponseDto = this.toUserLoginResponseDto(user);

        ApiResponse result = ApiResponse.success(userLoginResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/users/logout")
    @Operation(summary = "로그아웃", description = "로그아웃을 합니다.")
    public ResponseEntity<ApiResponse> logout(
            HttpServletRequest request
    ) {
        request.getSession(false).invalidate();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("로그아웃 성공"));
    }

    // Read - Get
    // 유저정보조회
    @Operation(summary = "유저 조회", description = "ID로 한명의 유저를 찾습니다.")
    @GetMapping("/public/users/{userId}")
    public ResponseEntity<ApiResponse> getUserById(
            @PathVariable Long userId
    ) {
        UserInfoResponseDto user = userService.getUserById(userId);

        ApiResponse result = ApiResponse.success(user);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 한 유저의 포스팅들 조회
    @GetMapping("/public/users/{userId}/posts")
    @Operation(summary = "유저 포스팅 조회", description = "유저의 모든 포스팅을 조회합니다.")
    public ResponseEntity<ApiResponse> findPostsByUser(
            @PathVariable Long userId,
            @Validated PostPageRequestParams params
    ) {
        Sort sort = params.getDirection().equalsIgnoreCase("desc") ?
                Sort.by(params.getSortBy()).descending() : Sort.by(params.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(params.getPage(), params.getSize(), sort);

        Page<UserPostsResponseDto> userPostsResponseDtoPage = userService.findPostsByUser(userId, pageable);

        ApiResponse result = ApiResponse.success(userPostsResponseDtoPage);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 한 유저의 댓글들 조회
    @GetMapping("/users/{userId}/comments")
    @Operation(summary = "유저 댓글 조회", description = "유저의 모든 댓글을 조회합니다.")
    public ResponseEntity<ApiResponse> findCommentsByUser(
            @PathVariable Long userId,
            @Validated DatePageRequestParams params
    ) {
        Sort sort = params.getDirection().equalsIgnoreCase("desc") ?
                Sort.by(params.getSortBy()).descending() : Sort.by(params.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(params.getPage(), params.getSize(), sort);

        Page<UserCommentResponseDto> commentsByUser = userService.findCommentsByUser(userId, pageable);

        ApiResponse result = ApiResponse.success(commentsByUser);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 한 유저의 좋아요 누른 게시물 조회
    @GetMapping("/users/{userId}/likes")
    @Operation(summary = "좋아요 누른 게시물 조회", description = "유저가 좋아요를 누른 모든 게시물을 조회합니다.")
    public ResponseEntity<ApiResponse> findAllPostLike(
            @PathVariable Long userId,
            @Validated DatePageRequestParams params
    ) {
        Sort sort = params.getDirection().equalsIgnoreCase("desc") ?
                Sort.by(params.getSortBy()).descending() : Sort.by(params.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(params.getPage(), params.getSize(), sort);

        Page<UserPostLikeResponseDto> userPostLikeResponseDto = userService.findAllPostLike(userId, pageable);

        ApiResponse result = ApiResponse.success(userPostLikeResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 한 유저의 친구목록 조회
    @GetMapping("/users/friends")
    @Operation(summary = "친구목록 조회", description = "유저의 모든 친구를 조회합니다.")
    public ResponseEntity<ApiResponse> findMyFriends(
            @Validated DatePageRequestParams params,
            HttpServletRequest request
    ) {
        User user = this.returnUserOfRequest(request);

        Pageable pageable = PageRequest.of(params.getPage(), params.getSize());

        Page<UserFriendsResponseDto> userFriendsResponseDto = userService.findMyFriends(user.getId(), pageable);

        ApiResponse result = ApiResponse.success(userFriendsResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 한 유저의 최근 읽은 게시글 목록 조회
    @GetMapping("/users/post-views")
    @Operation(summary = "최근 읽은 게시글 조회", description = "유저의 최근 읽은 게시글을 조회합니다.")
    public ResponseEntity<ApiResponse> findUserPostRecentViews(
            @Validated DatePageRequestParams params,
            HttpServletRequest request
    ) {
        User user = this.returnUserOfRequest(request);

        Sort sort = params.getDirection().equalsIgnoreCase("desc") ?
                Sort.by(params.getSortBy()).descending() : Sort.by(params.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(params.getPage(), params.getSize(), sort);

        Page<UserPostsResponseDto> userPostsResponseDtoPage = userService.findUserPostRecentViews(user.getId(), pageable);

        ApiResponse result = ApiResponse.success(userPostsResponseDtoPage);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 친구의 게시물만 모아서 조회
    @GetMapping("/users/friend-posts")
    @Operation(summary = "친구들 게시물만 조회", description = "친구들의 게시글을 조회합니다.")
    public ResponseEntity<ApiResponse> findMyFriendPosts(
            @Validated DatePageRequestParams params,
            HttpServletRequest request
    ) {
        User user = this.returnUserOfRequest(request);

        Sort sort = params.getDirection().equalsIgnoreCase("desc") ?
                Sort.by(params.getSortBy()).descending() : Sort.by(params.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(params.getPage(), params.getSize(), sort);

        Page<UserPostsResponseDto> userPostsResponseDtoPage = userService.findMyFriendPosts(user.getId(), pageable);

        ApiResponse result = ApiResponse.success(userPostsResponseDtoPage);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    // Update - Patch,Put
    // 비밀번호변경
    @PatchMapping("/users/me/password")
    @Operation(summary = "비밀번호 변경", description = "유저의 비밀번호를 변경합니다.")
    public ResponseEntity<ApiResponse> changePassword(
            @RequestBody @Valid UserChangePasswordDto dto,
            HttpServletRequest request
    ) {
        User loginUser = this.returnUserOfRequest(request);

        userService.changePassword(loginUser.getId(), dto);

        request.getSession(false).invalidate();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("비밀번호 변경완료"));
    }

    @PatchMapping("/users/me/profile")
    @Operation(summary = "프로필 변경", description = "유저의 프로필을 변경합니다")
    public ResponseEntity<ApiResponse> updateUserNickname(
            @RequestBody @Valid UserChangeProfileRequestDto dto,
            HttpServletRequest request
    ) {
        User user = this.returnUserOfRequest(request);

        userService.updateUserNickname(user.getId(), dto);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("프로필 변경완료"));
    }

    // Delete - Delete
    // 유저삭제, 탈퇴
    @DeleteMapping("/users/me")
    @Operation(summary = "유저 탈퇴", description = "유저가 탈퇴합니다.")
    public ResponseEntity<ApiResponse> deleteUser(
            @RequestBody @Valid UserDeleteRequestDto dto,
            HttpServletRequest request
    ) {
        User loginUser = this.returnUserOfRequest(request);

        userService.deleteUser(loginUser.getId(), dto);

        request.getSession(false).invalidate();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("회원 탈퇴완료"));
    }

    private User returnUserOfRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        return (User) session.getAttribute(SessionAttributeKeys.USER);
    }

    private UserLoginResponseDto toUserLoginResponseDto(User user) {
        return new UserLoginResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole().getName()
        );
    }

}
