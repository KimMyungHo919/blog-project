package com.project.blog.domain.user.controller;

import com.project.blog.domain.user.dto.request.*;
import com.project.blog.domain.user.dto.response.UserInfoResponseDto;
import com.project.blog.domain.user.dto.response.UserLoginResponseDto;
import com.project.blog.domain.user.dto.response.UserPostsResponseDto;
import com.project.blog.domain.user.dto.response.UserSignupResponseDto;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Create - Post
    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponseDto> signupUser(
            @Valid @RequestBody UserSignupRequestDto dto
    ) {
        UserSignupResponseDto result = userService.signupUser(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> loginUser(
            @Valid @RequestBody UserLoginRequestDto dto,
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

    // Read - Get
    // 유저정보조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponseDto> getUserById(
            @PathVariable Long userId
    ) {
        UserInfoResponseDto result = userService.getUserById(userId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 한 유저의 포스팅들 조회
    @GetMapping("/{userId}/posts")
    public ResponseEntity<Page<UserPostsResponseDto>> findPostsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserPostsResponseDto> result = userService.findPostsByUser(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Update - Patch,Put
    // 비밀번호변경
    @PatchMapping("/me/password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody UserChangePasswordDto dto,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User loginUser = (User) session.getAttribute(SessionAttributeKeys.USER);

        userService.changePassword(loginUser.getId(), dto);

        session.invalidate();

        return ResponseEntity.status(HttpStatus.OK).body("비밀번호 변경 완료. 다시 로그인해주세요");
    }

    @PatchMapping("/me/nickname")
    public ResponseEntity<String> updateUserNickname(
            @RequestBody UserChangeNicknameDto dto,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        userService.updateUserNickname(user.getId(), dto);

        return ResponseEntity.status(HttpStatus.OK).body("닉네임 변경완료");
    }

    // Delete - Delete
    // 유저삭제, 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteUser(
            @RequestBody UserDeleteRequestDto dto,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User loginUser = (User) session.getAttribute(SessionAttributeKeys.USER);

        userService.deleteUser(loginUser.getId(), dto);

        session.invalidate();

        return ResponseEntity.status(HttpStatus.OK).body("회원탈퇴가 완료되었습니다.");
    }
}
