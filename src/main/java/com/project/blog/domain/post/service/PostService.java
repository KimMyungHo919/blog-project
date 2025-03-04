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

/**
 * 게시글 관련 비즈니스 로직을 담당하는 서비스 클래스.
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private static final int MAX_RETRY = 10; // 락 획득 최대시도 횟수

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final RedissonClient redissonClient;
    private final ImageRepository imageRepository;
    private final PostViewRepository postViewRepository;

    private final Random random = new Random();

    /**
     * 게시글을 생성하는 메서드.
     *
     * @param userId 사용자 ID
     * @param dto    게시글 요청 DTO
     * @return 생성된 게시글의 응답 DTO
     */
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

    /**
     * 특정 게시글을 조회하는 메서드.
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID (로그인하지 않은 경우 null 가능)
     * @return 조회된 게시글의 응답 DTO
     * @throws InterruptedException 락 획득 실패 시 예외 발생
     */
    @Transactional
    public PostResponseDto findPost(Long postId, Long userId) throws InterruptedException {
        Post post = postRepository.findByPostWithUserOrElseThrow(postId);

        this.validateAccess(post, userId);

        RLock lock = acquireLock(postId);

        try {
            if (userId != null) {
                increaseViewsForLoggedInUser(post, userId);
            } else {
                post.increaseViews();
            }
            return PostResponseDto.fromEntity(post);
        } finally {
            releaseLock(lock);
        }
    }

    /**
     * 모든 공개 게시글을 조회하는 메서드.
     *
     * @param pageable 페이지네이션 정보
     * @return 게시글 응답 DTO 페이지
     */
    public Page<PostResponseDto> findAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAllPosts(pageable);

        return posts.map(PostResponseDto::fromEntity);
    }

    /**
     * 게시글을 수정하는 메서드.
     *
     * @param userId 사용자 ID
     * @param postId 게시글 ID
     * @param dto    게시글 수정 요청 DTO
     */
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

    /**
     * 게시글을 삭제하는 메서드.
     *
     * @param userId 사용자 ID
     * @param postId 게시글 ID
     */
    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findByIdAndUserId(postId, userId).orElseThrow(() -> new CustomException(ExceptionType.POST_NOT_FOUND));

        List<String> imageUrls = extractImageUrls(post.getContent()); // 요청본문에 이미지 url 을 리스트로 저장
        imageRepository.updateTypeNullByImageUrl(imageUrls);

        postRepository.deleteById(postId);
    }

    /**
     * 특정 포스팅의 모든 댓글을 조회합니다.
     *
     * @param postId  조회할 포스팅의 ID
     * @param userId  요청한 사용자의 ID (비공개 포스팅 접근 검증용)
     * @param pageable 페이지 정보
     * @return 댓글 목록 (페이지네이션 포함)
     */
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

    /**
     * 특정 포스팅의 좋아요를 누른 유저 정보를 조회합니다.
     *
     * @param postId  조회할 포스팅의 ID
     * @param userId  요청한 사용자의 ID (비공개 포스팅 접근 검증용)
     * @param pageable 페이지 정보
     * @return 좋아요 누른 유저 목록 (페이지네이션 포함)
     */
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

    /**
     * 사용자의 비공개 포스팅 목록을 조회합니다.
     *
     * @param loginUserId 로그인한 사용자의 ID
     * @param pageable 페이지 정보
     * @return 비공개 포스팅 목록 (페이지네이션 포함)
     */
    public Page<PostResponseDto> findMyPrivatePost(Long loginUserId, Pageable pageable) {
        Page<Post> posts = postRepository.findMyPrivatePost(loginUserId, pageable);

        return posts.map(PostResponseDto::fromEntity);
    }

    /**
     * 사용자의 임시 저장된 포스팅 목록을 조회합니다.
     *
     * @param loginUserId 로그인한 사용자의 ID
     * @param pageable 페이지 정보
     * @return 임시 저장된 포스팅 목록 (페이지네이션 포함)
     */
    public Page<PostResponseDto> findMyDraftPost(Long loginUserId, Pageable pageable) {
        Page<Post> posts = postRepository.findMyDraftPost(loginUserId, pageable);

        return posts.map(PostResponseDto::fromEntity);
    }

    /**
     * 포스팅 제목을 기준으로 검색합니다.
     *
     * @param title 검색할 제목 문자열
     * @param pageable 페이지 정보
     * @return 검색된 포스팅 목록 (페이지네이션 포함)
     */
    public Page<PostResponseDto> searchTitle(String title, Pageable pageable) {
        Page<Post> postPage = postRepository.findByTitlePage(title, pageable);

        return postPage.map(PostResponseDto::fromEntity);
    }

    /**
     * 최신 인기글 10개를 조회합니다. (최근 1주일 내 데이터 기준)
     *
     * @param pageable 페이지 정보
     * @return 인기 포스팅 목록 (페이지네이션 포함)
     */
    public Page<PostResponseDto> topTenPosts(Pageable pageable) {
        Page<Post> postPage = postRepository.findTopTenPosts(LocalDateTime.now().minusWeeks(1), pageable);

        return postPage.map(PostResponseDto::fromEntity);
    }


    /**
     * 게시글 내용에서 이미지 URL 을 추출하는 메서드.
     *
     * @param content 게시글 내용
     * @return 이미지 URL 목록
     */
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

    /**
     * 사용자가 특정 게시글에 접근할 수 있는지 검증합니다.
     * - 게시글이 비공개(PRIVATE) 또는 임시글(DRAFT)일 경우, 작성자만 접근 가능하도록 제한합니다.
     * - 작성자가 아닌 사용자가 접근하려 하면 {@code CustomException}을 발생시킵니다.
     *
     * @param post   접근하려는 게시글 객체
     * @param userId 현재 요청을 보낸 사용자의 ID
     * @throws CustomException 사용자가 게시글에 접근할 권한이 없을 경우 발생
     */
    private void validateAccess(Post post, Long userId) {
        if (Objects.equals(post.getPostVisibility(), PostVisibility.PRIVATE) || Objects.equals(post.getPostVisibility(), PostVisibility.DRAFT)) {
            if (!Objects.equals(post.getUser().getId(), userId)) {
                throw new CustomException(ExceptionType.PRIVATE_POST);
            }
        }
    }

    /**
     * 특정 게시글에 대한 락을 획득하는 메서드.
     *
     * @param postId 게시글 ID
     * @return Redisson 락 객체
     * @throws InterruptedException 락 획득 실패 시 예외 발생
     */
    private RLock acquireLock(Long postId) throws InterruptedException {
        RLock lock = redissonClient.getLock("post:lock" + postId);

        int retryCount = 0;
        boolean isLocked;

        while (retryCount < MAX_RETRY) {
            int waitTime = 100 + random.nextInt(100);
            isLocked = lock.tryLock(5000, 3000, TimeUnit.MILLISECONDS);
            if (isLocked) {
                return lock;
            }
            retryCount++;
            Thread.sleep(waitTime);
        }

        throw new RuntimeException("락 획득 실패 : 너무 많은 요청");
    }

    /**
     * Redisson 락을 해제하는 메서드.
     *
     * @param lock Redisson 락 객체
     */
    private void releaseLock(RLock lock) {
        if (lock.isHeldByCurrentThread() && lock.isLocked()) {
            lock.unlock();
        }
    }

    /**
     * 로그인한 유저의 게시글 조회처리 메서드.
     *
     * @param post Post 객체
     * @param userId 유저아이디
     */
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

}
