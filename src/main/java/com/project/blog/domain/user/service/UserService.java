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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    private final ImageRepository imageRepository;

    // 회원가입
    @Transactional
    public UserSignupResponseDto signupUser(UserSignupRequestDto dto) throws MessagingException {
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
                Role.from(dto.getRole())
        );
        user.setVerified(false); // 초기 인증안됨 설정.

        if (dto.getProfileImageUrl() != null) {
            user.setProfile(dto.getImageId(), dto.getProfileImageUrl());
            imageRepository.updateUserTypeByImgUrls(dto.getProfileImageUrl(), ImageType.PROFILE);
        }

        // 인증이메일 발송
        String token = emailSenderService.sendVerificationEmail(dto.getEmail());
        user.setVerificationToken(token); // 발급된 토큰 set
        user.setTokenExpiryTime(LocalDateTime.now().plusMinutes(10)); // 토큰 유효시간: 10분

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

        // 이메일 미인증상태일경우
        if (!user.isVerified()) {
            throw new CustomException(ExceptionType.EMAIL_NOT_AUTHORIZED);
        }

        return user;
    }

    // 유저정보조회
    public UserInfoResponseDto getUserById(Long userId) {
        User user = userRepository.findByIdOrElseThrow(userId);

        return new UserInfoResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getImageId(),
                user.getProfileImageUrl()
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

    // 프로필 변경
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
        imageRepository.updateUserTypeByImgUrls(dto.getProfileImageUrl(), ImageType.PROFILE);
    }

    // 탈퇴, 유저삭제
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

    // 한 유저의 posts 조회
    public Page<UserPostsResponseDto> findPostsByUser(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ExceptionType.USER_NOT_FOUND);
        }

        Page<Post> posts = postRepository.findAllPostsWithUser(userId, pageable);

        return posts.map(
                post -> new UserPostsResponseDto(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getUser().getNickname(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()
                )
        );
    }

    // 한 유저의 comments 조회
    public Page<UserCommentResponseDto> findCommentsByUser(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ExceptionType.USER_NOT_FOUND);
        }

        Page<Comment> comments = commentRepository.findAllCommentsWithUser(userId, pageable);

        return comments.map(
                comment -> new UserCommentResponseDto(
                        comment.getId(),
                        comment.getComment(),
                        comment.getUser().getNickname(),
                        comment.getCreatedAt(),
                        comment.getUpdatedAt()
                )
        );
    }

    // 한 유저의 좋아요 누른 게시물 조회
    public Page<UserPostLikeResponseDto> findAllPostLike(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ExceptionType.USER_NOT_FOUND);
        }

        Page<Post> posts = postLikeRepository.findLikedPostsByUser(userId, pageable);

        return posts.map(
                post -> new UserPostLikeResponseDto(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()
                )
        );
    }

    // 한 유저의 친구목록 조회
    public Page<UserFriendsResponseDto> findMyFriends(Long id, Pageable pageable) {
        if (!userRepository.existsById(id)) {
            throw new CustomException(ExceptionType.USER_NOT_FOUND);
        }

        Page<Friend> friends = friendRepository.findMyFriends(id, pageable);

        return friends.map(
                friend -> {
                    User friendUser = (friend.getSender().getId().equals(id)) ? friend.getReceiver() : friend.getSender();
                    return new UserFriendsResponseDto(
                            friendUser.getId(),
                            friendUser.getNickname(),
                            friendUser.getEmail()
                    );
                }
        );
    }
}
