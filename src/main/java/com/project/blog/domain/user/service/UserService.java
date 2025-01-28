package com.project.blog.domain.user.service;

import com.project.blog.domain.user.dto.request.UserSignupRequestDto;
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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserSignupResponseDto signupUser(UserSignupRequestDto dto) {
        // 이미 해당 이메일이 존재하는지 확인한다.
        Boolean isUserEmail = userRepository.existsByEmail(dto.getEmail());
        if (isUserEmail) {
            throw new CustomException(ExceptionType.EXIST_USER);
        }

        // User 객체 만들기 -> 비밀번호 엄호화
        User user = new User(
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getNickName(),
                Role.USER
        );

        // 해당 이메일이 데이터베이스에 없으면 회원가입을 해준다.
        userRepository.save(user);

        // UserSignupResponseDto 로 반환
        return new UserSignupResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickName()
        );
    }
}
