package com.project.blog.domain.post.service;

import com.project.blog.domain.comment.entity.Comment;
import com.project.blog.domain.comment.repository.CommentRepository;
import com.project.blog.domain.post.dto.request.PostRequestDto;
import com.project.blog.domain.post.dto.request.PostUpdateRequestDto;
import com.project.blog.domain.post.dto.response.PostCommentsResponseDto;
import com.project.blog.domain.post.dto.response.PostLikesUserResponseDto;
import com.project.blog.domain.post.dto.response.PostResponseDto;
import com.project.blog.domain.post.entity.Post;
import com.project.blog.domain.post.repository.PostRepository;
import com.project.blog.domain.postlike.repository.PostLikeRepository;
import com.project.blog.domain.image.repository.ImageRepository;
import com.project.blog.domain.postview.entity.PostView;
import com.project.blog.domain.postview.repository.PostViewRepository;
import com.project.blog.domain.user.entity.User;
import com.project.blog.domain.user.repository.UserRepository;
import com.project.blog.global.enums.PostVisibility;
import com.project.blog.global.exception.business.CustomException;
import com.project.blog.global.exception.enums.ExceptionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final int MAX_RETRY = 7; // 락 획득 최대시도 횟수

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final RedissonClient redissonClient;
    private final ImageRepository imageRepository;
    private final PostViewRepository postViewRepository;

    private final Random random = new Random();

    // 포스팅 작성
    @Transactional
    public PostResponseDto createPost(Long userId, PostRequestDto dto) {
        User user = userRepository.findByIdOrElseThrow(userId);

        Post post = new Post(
                dto.getTitle(),
                dto.getContent(),
                PostVisibility.from(dto.getPostVisibility())
        );

        post.setUser(user);

        postRepository.save(post);

        return PostResponseDto.fromEntity(post);
    }

    // 글 조회 -> 하나의 포스팅만 조회
    @Transactional
    public PostResponseDto findPost(Long postId, Long userId) throws InterruptedException {
        Post post = postRepository.findByPostWithUserOrElseThrow(postId);

        this.validateAccess(post, userId);

        RLock lock = acquireLock(postId);

        try {
            if (userId != null) {
                increaseViewsForLoggedInUser(post, userId);
            } else {
                increaseViewsForGuest(post);
            }
            return toPostResponseDto(post);
        } finally {
            releaseLock(lock);
        }
    }

    // 글 조회 -> 모든 공개 포스팅 조회
    public Page<PostResponseDto> findAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAllPosts(pageable);

        return posts.map(PostResponseDto::fromEntity);
    }

    // 글 업데이트 - 제목,내용
    @Transactional
    public void updatePost(Long userId, Long postId, PostUpdateRequestDto dto) {
        Post post = postRepository.findByIdOrElseThrow(postId);

        if (!Objects.equals(userId, post.getUser().getId())) {
            throw new CustomException(ExceptionType.USER_NOT_MATCH);
        }

        post.updateTitle(dto.getTitle());
        post.updateContent(dto.getContent());
        post.changeIsVisibility(PostVisibility.from(dto.getPostVisibility()));
    }

    // 글 삭제
    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findByIdAndUserId(postId, userId).orElseThrow(() -> new CustomException(ExceptionType.POST_NOT_FOUND));

        List<String> imageUrls = extractImageUrls(post.getContent()); // 요청본문에 이미지 url 을 리스트로 저장
        imageRepository.updateTypeNullByImageUrl(imageUrls);

        postRepository.deleteById(postId);
    }

    // 한 포스팅의 댓글 전체조회
    public Page<PostCommentsResponseDto> findAllCommentsOfPost(Long postId, Long userId, Pageable pageable) {
        Post post = postRepository.findByIdOrElseThrow(postId);

        if (Objects.equals(post.getPostVisibility(), PostVisibility.PRIVATE)) {
            if (!Objects.equals(post.getUser().getId(), userId)) {
                throw new CustomException(ExceptionType.PRIVATE_POST);
            }
        }

        Page<Comment> comments = commentRepository.findAllCommentsWithPost(postId, pageable);

        return comments.map(PostCommentsResponseDto::fromEntity);
    }

    // 한 포스팅의 좋아요 누른 유저의 정보 조회
    public Page<PostLikesUserResponseDto> findAllLikesUserData(Long postId, Long userId, Pageable pageable) {
        Post post = postRepository.findByIdOrElseThrow(postId);

        if (Objects.equals(post.getPostVisibility(), PostVisibility.PRIVATE)) {
            if (!Objects.equals(post.getUser().getId(), userId)) {
                throw new CustomException(ExceptionType.PRIVATE_POST);
            }
        }

        Page<User> users = postLikeRepository.findPostLikesByUserData(postId, pageable);

        return users.map(
                user -> new PostLikesUserResponseDto(user.getNickname())
        );
    }

    // 나의 비밀글 조회
    public Page<PostResponseDto> findMyPrivatePost(Long loginUserId, Pageable pageable) {
        Page<Post> posts = postRepository.findMyPrivatePost(loginUserId, pageable);

        return posts.map(PostResponseDto::fromEntity);
    }

    public Page<PostResponseDto> findMyDraftPost(Long loginUserId, Pageable pageable) {
        Page<Post> posts = postRepository.findMyDraftPost(loginUserId, pageable);

        return posts.map(PostResponseDto::fromEntity);
    }

    // 포스팅 타이틀로 검색기능
    public Page<PostResponseDto> searchTitle(String title, Pageable pageable) {
        Page<Post> postPage = postRepository.findByTitlePage(title, pageable);

        return postPage.map(PostResponseDto::fromEntity);
    }


    // 요청본문에서 이미지 url 을 리스트로 저장해서 리턴해주는 메소드
    public static List<String> extractImageUrls(String content) {
        List<String> imageUrls = new ArrayList<>();

        // <img> 태그에서 src 속성 값만 추출하는 정규식
        Pattern pattern = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            imageUrls.add(matcher.group(1)); // src 속성 값만 추가
        }

        return imageUrls;
    }

    private void validateAccess(Post post, Long userId) {
        if (Objects.equals(post.getPostVisibility(), PostVisibility.PRIVATE) || Objects.equals(post.getPostVisibility(), PostVisibility.DRAFT)) {
            if (!Objects.equals(post.getUser().getId(), userId)) {
                throw new CustomException(ExceptionType.PRIVATE_POST);
            }
        }
    }

    private RLock acquireLock(Long postId) throws InterruptedException {
        RLock lock = redissonClient.getLock("post:lock" + postId);

        int retryCount = 0;
        boolean isLocked;

        while (retryCount < MAX_RETRY) {
            int waitTime = 200 + random.nextInt(100);
            isLocked = lock.tryLock(5000, 1000, TimeUnit.MILLISECONDS);
            if (isLocked) {
                return lock;
            }
            retryCount++;
            Thread.sleep(waitTime);
        }

        throw new RuntimeException("락 획득 실패 : 너무 많은 요청");
    }

    // Redisson 락 해제
    private void releaseLock(RLock lock) {
        if (lock.isHeldByCurrentThread() && lock.isLocked()) {
            lock.unlock();
        }
    }

    // 로그인한 유저의 조회 처리
    private void increaseViewsForLoggedInUser(Post post, Long userId) {
        if (!Objects.equals(post.getUser().getId(), userId) &&
                !postViewRepository.existsByUserIdAndPostId(userId, post.getId())) {

            User user = userRepository.findByIdOrElseThrow(userId);
            PostView postView = new PostView(LocalDateTime.now());
            postView.setPost(post);
            postView.setUser(user);
            post.increaseViews();

            postViewRepository.save(postView);
        }
    }

    // 비로그인 유저의 조회 처리
    private void increaseViewsForGuest(Post post) {
        post.increaseViews();
    }

    // DTO 변환
    private PostResponseDto toPostResponseDto(Post post) {
        long postLikesSize = postLikeRepository.sizeOfPost(post.getId());

        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViews(),
                postLikesSize,
                post.getUser().getNickname(),
                post.getPostVisibility().getValue(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

}
