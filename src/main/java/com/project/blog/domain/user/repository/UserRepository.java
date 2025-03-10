package com.project.blog.domain.user.repository;

import com.project.blog.domain.user.entity.User;
import com.project.blog.global.exception.business.CustomException;
import com.project.blog.global.exception.enums.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    default User findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    User findByVerificationToken(String token);

    boolean existsByNickname(String nickname);
}
