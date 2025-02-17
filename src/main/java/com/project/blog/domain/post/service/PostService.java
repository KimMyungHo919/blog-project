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

        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViews(),
                post.getPostLikes().size(),
                post.getUser().getNickname(),
                post.getPostVisibility().getValue(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    // 요청본문에서 이미지 url 을 리스트로 저장해서 리턴해주는 메소드
    private List<String> extractImageUrls(String content) {
        List<String> imageUrls = new ArrayList<>();

        // 버킷 이름을 포함한 정규식
        Pattern pattern = Pattern.compile("https://[a-zA-Z0-9-]+\\.s3\\.[a-zA-Z0-9-]+\\.amazonaws\\.com/[^\\s\"']+");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            imageUrls.add(matcher.group());
        }

        return imageUrls;
    }

    // 글 조회 -> 하나의 포스팅만 조회
    @Transactional
    public PostResponseDto findPost(Long postId, Long userId) throws InterruptedException {
        Post post = postRepository.findByPostWithUserOrElseThrow(postId);

        if (Objects.equals(post.getPostVisibility(), PostVisibility.PRIVATE)) {
            if (!Objects.equals(post.getUser().getId(), userId)) {
                throw new CustomException(ExceptionType.PRIVATE_POST);
            }
        }

        RLock rLock = redissonClient.getLock("post:lock" + postId); // postId로 고유 락 생성

        int retryCount = 0; // 락 획득 재시도 카운트
        boolean isLocked = false; // 획득 여부

        while (retryCount < MAX_RETRY) {
            int waitTime = 100 + random.nextInt(200); // 랜덤 대기시간 설정
            isLocked = rLock.tryLock(5000, 2000, TimeUnit.MILLISECONDS); // 락 획득 시도
            if (isLocked) {
                break; // 락 획득하면 while 문 벗어남
            }
            retryCount++; // 락 획득 실패하면 카운트 +1
            Thread.sleep(waitTime);
        }

        if (!isLocked) { // 3번다 실패하면 에러처리
            // 락 획득 실패 시 로그를 기록하고 예외처리
            throw new RuntimeException("락 획득 실패 : 너무 많은 요청");
        }

        try {
            if (!Objects.equals(post.getUser().getId(), userId)) {
                post.increaseViews();
            }

            long postLikesSize = postLikeRepository.sizeOfPost(postId);

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
        } finally {
            if (rLock.isHeldByCurrentThread() && rLock.isLocked()) {
                rLock.unlock();
            }
        }
    }

    // 글 조회 -> 모든 공개 포스팅 조회
    public Page<PostResponseDto> findAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAllPosts(pageable);

        return posts.map(
                post -> new PostResponseDto(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getViews(),
                        post.getPostLikes().size(),
                        post.getUser().getNickname(),
                        post.getPostVisibility().getValue(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()
                )
        );
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

        return comments.map(
                comment -> new PostCommentsResponseDto(
                        comment.getId(),
                        comment.getComment(),
                        comment.getUser().getNickname(),
                        comment.getCreatedAt(),
                        comment.getUpdatedAt()
                )
        );
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
                user -> new PostLikesUserResponseDto(
                        user.getNickname()
                )
        );
    }

    // 나의 비밀글 조회
    public Page<PostResponseDto> findMyPrivatePost(Long loginUserId, Pageable pageable) {
        Page<Post> posts = postRepository.findMyPrivatePost(loginUserId, pageable);

        return posts.map(
                post -> new PostResponseDto(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getViews(),
                        post.getPostLikes().size(),
                        post.getUser().getNickname(),
                        post.getPostVisibility().getValue(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()
                )
        );
    }

    // 포스팅 타이틀로 검색기능
    public Page<PostResponseDto> searchTitle(String title, Pageable pageable) {
        Page<Post> postPage = postRepository.findByTitlePage(title, pageable);

        return postPage.map(
                post -> new PostResponseDto(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getViews(),
                        post.getPostLikes().size(),
                        post.getUser().getNickname(),
                        post.getPostVisibility().getValue(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()
                )
        );
    }


}
