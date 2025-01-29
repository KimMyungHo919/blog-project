package com.project.blog.domain.user.controller;

import com.project.blog.domain.user.dto.request.UserChangePasswordDto;
import com.project.blog.domain.user.dto.request.UserDeleteRequestDto;
import com.project.blog.domain.user.dto.request.UserLoginRequestDto;
import com.project.blog.domain.user.dto.request.UserSignupRequestDto;
import com.project.blog.domain.user.dto.response.UserLoginResponseDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Create - Post
    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponseDto> signupUser(
            @Valid @RequestBody UserSignupRequestDto dto
    ) {
        UserSignupResponseDto result = userService.signupUser(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

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
                user.getNickName(),
                user.getRole()
        );

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Read - Get

    // Update - Patch,Put
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

    // Delete - Delete
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
