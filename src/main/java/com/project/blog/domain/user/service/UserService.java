package com.project.blog.domain.user.service;

import com.project.blog.domain.user.dto.request.*;
import com.project.blog.domain.user.dto.response.UserInfoResponseDto;
import com.project.blog.domain.user.dto.response.UserSignupResponseDto;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.enums.Role;
import com.project.blog.domain.user.repository.UserRepository;
import com.project.blog.global.encoder.PasswordEncoder;
import com.project.blog.global.exception.CustomException;
import com.project.blog.global.exception.ExceptionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public UserSignupResponseDto signupUser(UserSignupRequestDto dto) {
        // 이미 해당 이메일이 존재하는지 확인한다.
        boolean isUserEmail = userRepository.existsByEmail(dto.getEmail());
        if (isUserEmail) {
            throw new CustomException(ExceptionType.EXIST_USER);
        }

        // User 객체 만들기 -> 비밀번호 엄호화
        User user = new User(
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getNickname(),
                Role.USER
        );

        // 해당 이메일이 데이터베이스에 없으면 회원가입을 해준다.
        userRepository.save(user);

        // UserSignupResponseDto 로 반환
        return new UserSignupResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );
    }

    // 로그인
    @Transactional
    public User loginUser(UserLoginRequestDto dto) {
        // 이메일로 유저확인
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND)
        );

        // 비밀번호 일치확인
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CustomException(ExceptionType.PASSWORD_NOT_CORRECT);
        }

        return user;
    }

    // 유저정보조회
    public UserInfoResponseDto getUserById(Long userId) {
        User user = userRepository.findByIdOrElseThrow(userId);

        return new UserInfoResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(Long id, UserChangePasswordDto dto) {
        User user = userRepository.findByIdOrElseThrow(id);

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new CustomException(ExceptionType.PASSWORD_NOT_CORRECT);
        }

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new CustomException(ExceptionType.PASSWORD_SAME);
        }

        String encodeNewPassword = passwordEncoder.encode(dto.getNewPassword());

        user.changePassword(encodeNewPassword);
    }

    // 닉네임 변경
    @Transactional
    public void updateUserNickname(Long id, UserChangeNicknameDto dto) {
        User user = userRepository.findByIdOrElseThrow(id);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CustomException(ExceptionType.PASSWORD_NOT_CORRECT);
        }

        if (Objects.equals(user.getNickname(), dto.getNickname())) {
            throw new CustomException(ExceptionType.ALREADY_SAME_NICKNAME);
        }

        user.changeNickname(dto.getNickname());
    }

    // 탈퇴, 유저삭제
    @Transactional
    public void deleteUser(Long id, UserDeleteRequestDto dto) {
        User user = userRepository.findByIdOrElseThrow(id);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CustomException(ExceptionType.PASSWORD_NOT_CORRECT);
        }

        userRepository.delete(user);
    }

}
