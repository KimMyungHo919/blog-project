package com.project.blog.domain.user.service;

import com.project.blog.domain.comment.entity.Comment;
import com.project.blog.domain.comment.repository.CommentRepository;
import com.project.blog.domain.email.EmailSenderService;
import com.project.blog.domain.friend.entity.Friend;
import com.project.blog.domain.friend.repository.FriendRepository;
import com.project.blog.domain.image.repository.ImageRepository;
import com.project.blog.domain.image.service.ImageService;
import com.project.blog.domain.post.entity.Post;
import com.project.blog.domain.post.repository.PostRepository;
import com.project.blog.domain.postlike.repository.PostLikeRepository;
import com.project.blog.domain.postview.repository.PostViewRepository;
import com.project.blog.domain.rabbitmq.producer.RabbitUserSignupProducer;
import com.project.blog.domain.user.dto.request.*;
import com.project.blog.domain.user.dto.response.*;
import com.project.blog.domain.user.entity.User;
import com.project.blog.domain.user.repository.UserRepository;
import com.project.blog.global.encoder.PasswordEncoder;
import com.project.blog.global.enums.ImageType;
import com.project.blog.global.enums.Role;
import com.project.blog.global.exception.business.CustomException;
import com.project.blog.global.exception.enums.ExceptionType;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * 사용자 관련 비즈니스 로직을 담당하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final PasswordEncoder passwordEncoder;
    private final FriendRepository friendRepository;
    private final EmailSenderService emailSenderService;
    private final ImageService imageService;
    private final PostViewRepository postViewRepository;
    private final RabbitUserSignupProducer rabbitUserSignupProducer;

    /**
     * 회원가입을 수행합니다.
     * 인증이메일을 발송합니다.
     *
     * @param dto 회원가입 요청 DTO
     * @return 회원가입된 사용자 정보 DTO
     * @throws MessagingException 이메일 전송 실패 시 발생
     */
    @Transactional
    public UserSignupResponseDto signupUser(UserSignupRequestDto dto) throws MessagingException {
        this.isExistsUserEmailOrUserNickname(dto.getEmail(), dto.getNickname());

        User user = new User(
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getNickname(),
                Role.from(dto.getRole())
        );
        user.setVerified(false);

        if (dto.getProfileImageUrl() != null) {
            user.setProfile(dto.getImageId(), dto.getProfileImageUrl());
        }

        // 인증이메일 발송
        String token = emailSenderService.sendVerificationEmail(dto.getEmail());
        user.setVerificationToken(token); // 발급된 토큰 set
        user.setTokenExpiryTime(LocalDateTime.now().plusMinutes(10));

        userRepository.save(user);

        rabbitUserSignupProducer.userSignupEvent(user.getId(), user.getTokenExpiryTime());

        // UserSignupResponseDto 로 반환
        return UserSignupResponseDto.fromEntity(user);
    }

    /**
     * 사용자가 로그인합니다.
     *
     * @param dto 로그인 요청 DTO
     * @return 로그인된 사용자 객체
     */
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

        // 이메일 미인증상태일경우
        if (!user.isVerified()) {
            throw new CustomException(ExceptionType.EMAIL_NOT_AUTHORIZED);
        }

        return user;
    }

    /**
     * 사용자 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 정보 DTO
     */
    public UserInfoResponseDto getUserById(Long userId) {
        User user = userRepository.findByIdOrElseThrow(userId);

        return UserInfoResponseDto.fromEntity(user);
    }

    /**
     * 사용자의 비밀번호를 변경합니다.
     *
     * @param id 사용자 ID
     * @param dto 비밀번호 변경 요청 DTO
     */
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

    /**
     * 사용자의 닉네임 및 프로필 이미지를 변경합니다.
     *
     * @param id 사용자 ID
     * @param dto 프로필 변경 요청 DTO
     */
    @Transactional
    public void updateUserNickname(Long id, UserChangeProfileRequestDto dto) {
        User user = userRepository.findByIdOrElseThrow(id);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CustomException(ExceptionType.PASSWORD_NOT_CORRECT);
        }

        if (user.getProfileImageUrl() != null) {
            imageService.deleteImageFromS3(user.getProfileImageUrl());
        }

        user.changeNickname(dto.getNickname());
        user.setProfile(dto.getImageId(), dto.getProfileImageUrl());
    }

    /**
     * 사용자를 탈퇴 처리합니다.
     *
     * @param id 사용자 ID
     * @param dto 탈퇴 요청 DTO
     */
    @Transactional
    public void deleteUser(Long id, UserDeleteRequestDto dto) {
        User user = userRepository.findByIdOrElseThrow(id);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CustomException(ExceptionType.PASSWORD_NOT_CORRECT);
        }

        if (user.getProfileImageUrl() != null) {
            imageService.deleteImageFromS3(user.getProfileImageUrl());
        }

        userRepository.delete(user);
    }

    /**
     * 특정 사용자의 게시물 목록을 조회합니다.
     *
     * @param userId   조회할 사용자의 ID
     * @param pageable 페이지네이션 정보
     * @return 사용자의 게시물 목록 (페이징 처리됨)
     */
    public Page<UserPostsResponseDto> findPostsByUser(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ExceptionType.USER_NOT_FOUND);
        }

        Page<Post> posts = postRepository.findAllPostsWithUser(userId, pageable);

        return posts.map(UserPostsResponseDto::fromEntity);
    }

    /**
     * 특정 사용자의 댓글 목록을 조회합니다.
     *
     * @param userId   조회할 사용자의 ID
     * @param pageable 페이지네이션 정보
     * @return 사용자의 댓글 목록 (페이징 처리됨)
     */
    public Page<UserCommentResponseDto> findCommentsByUser(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ExceptionType.USER_NOT_FOUND);
        }

        Page<Comment> comments = commentRepository.findAllCommentsWithUser(userId, pageable);

        return comments.map(UserCommentResponseDto::fromEntity);
    }

    /**
     * 특정 사용자가 좋아요를 누른 게시물 목록을 조회합니다.
     *
     * @param userId   조회할 사용자의 ID
     * @param pageable 페이지네이션 정보
     * @return 사용자가 좋아요한 게시물 목록 (페이징 처리됨)
     */
    public Page<UserPostLikeResponseDto> findAllPostLike(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ExceptionType.USER_NOT_FOUND);
        }

        Page<Post> posts = postLikeRepository.findLikedPostsByUser(userId, pageable);

        return posts.map(UserPostLikeResponseDto::fromEntity);
    }

    /**
     * 특정 사용자의 친구 목록을 조회합니다.
     *
     * @param id       조회할 사용자의 ID
     * @param pageable 페이지네이션 정보
     * @return 사용자의 친구 목록 (페이징 처리됨)
     */
    public Page<UserFriendsResponseDto> findMyFriends(Long id, Pageable pageable) {
        if (!userRepository.existsById(id)) {
            throw new CustomException(ExceptionType.USER_NOT_FOUND);
        }

        Page<Friend> friends = friendRepository.findMyFriends(id, pageable);

        return friends.map(
                friend -> {
                    User friendUser = (friend.getSender().getId().equals(id)) ? friend.getReceiver() : friend.getSender();
                    return UserFriendsResponseDto.fromEntity(friendUser);
                }
        );
    }

    /**
     * 특정 사용자가 최근 조회한 게시물 목록을 조회합니다.
     *
     * @param loginUserId 조회할 사용자의 ID
     * @param pageable    페이지네이션 정보
     * @return 사용자가 최근 조회한 게시물 목록 (페이징 처리됨)
     */
    public Page<UserPostsResponseDto> findUserPostRecentViews(Long loginUserId, Pageable pageable) {
        Page<Post> posts = postViewRepository.findByUserIdRecentView(loginUserId, pageable);

        return posts.map(UserPostsResponseDto::fromEntity);
    }

    /**
     * 특정 사용자의 친구들이 작성한 게시물 목록을 조회합니다.
     *
     * @param loginUserId 조회할 사용자의 ID
     * @param pageable    페이지네이션 정보
     * @return 친구들이 작성한 게시물 목록 (페이징 처리됨)
     */
    public Page<UserPostsResponseDto> findMyFriendPosts(Long loginUserId, Pageable pageable) {
        Page<Post> posts = postRepository.findMyFriendPosts(loginUserId, pageable);

        return posts.map(
                post -> {
                    Post friendPost = (post.getUser().getId().equals(loginUserId)) ? (Post) Page.empty() : post;
                    return UserPostsResponseDto.fromEntity(friendPost);
                }
        );
    }

    /**
     * 이메일 또는 닉네임이 이미 존재하는지 확인합니다.
     *
     * @param email   확인할 이메일 주소
     * @param nickname 확인할 닉네임
     * @throws CustomException 이메일 또는 닉네임이 이미 존재하는 경우 예외 발생
     */
    private void isExistsUserEmailOrUserNickname(String email, String nickname) {
        boolean isUserEmail = userRepository.existsByEmail(email);
        if (isUserEmail) {
            throw new CustomException(ExceptionType.EXIST_USER);
        }

        boolean isUserNickname = userRepository.existsByNickname(nickname);
        if (isUserNickname) {
            throw new CustomException(ExceptionType.EXIST_NICKNAME);
        }
    }

}
