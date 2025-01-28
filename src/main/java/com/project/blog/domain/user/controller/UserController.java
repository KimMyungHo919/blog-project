package com.project.blog.domain.user.controller;

import com.project.blog.domain.user.dto.request.UserSignupRequestDto;
import com.project.blog.domain.user.dto.response.UserSignupResponseDto;
import com.project.blog.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // Read - Get

    // Update - Patch,Put

    // Delete - Delete
}
