package com.project.blog.domain.post.service;

import com.project.blog.domain.comment.entity.Comment;
import com.project.blog.domain.comment.repository.CommentRepository;
import com.project.blog.domain.post.dto.response.PostCommentsResponseDto;
import com.project.blog.domain.post.dto.response.PostLikesUserResponseDto;
import com.project.blog.domain.post.dto.response.PostResponseDto;
import com.project.blog.domain.post.entity.Post;
import com.project.blog.domain.post.repository.PostRepository;
import com.project.blog.domain.postlike.repository.PostLikeRepository;
import com.project.blog.domain.postview.repository.PostViewRepository;
import com.project.blog.domain.user.entity.User;
import com.project.blog.domain.user.repository.UserRepository;
import com.project.blog.global.enums.PostVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    private Pageable pageable;

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private PostViewRepository postViewRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setup() {
        this.pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("포스트 조회시 조회수 동시성제어 - 500번 동시 조회")
    public void testFindPostWithConcurrentAccess() throws InterruptedException {
        // given
        Long postId = 1L;
        Long userId = 1L;
        Long writingUser = 2L;

        Post post = new Post(postId, 0, PostVisibility.PUBLIC);
        User readUser = new User(writingUser, "testUser");
        post.setUser(readUser);

        // given
        given(postRepository.findByPostWithUserOrElseThrow(postId)).willReturn(post);
        given(postLikeRepository.sizeOfPost(postId)).willReturn(10L);
        given(postViewRepository.existsByUserIdAndPostId(anyLong(), anyLong())).willReturn(false);
        given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(readUser);

        // 레디슨 클라이언트의 락(mock) 생성
        RLock mockLock = mock(RLock.class);

        given(redissonClient.getLock("post:lock" + postId)).willReturn(mockLock);
        given(mockLock.tryLock(3, 1, TimeUnit.SECONDS)).willReturn(true);

        // when
        int concurrentRequests = 500; // 동시 요청 수
        ExecutorService executor = Executors.newFixedThreadPool(concurrentRequests); // 요청을 "병렬" 로 처리할 Executor 생성

        List<Callable<Void>> tasks = new ArrayList<>();
        // 2000번의 요청을 생성하여 tasks 리스트에 추가
        for (int i = 0; i < concurrentRequests; i++) {
            tasks.add(() -> {
                postService.findPost(postId, userId); // 각 스레드에서 포스트 조회 메소드 호출
                return null;
            });
        }

        // 모든 요청을 병렬로 실행
        executor.invokeAll(tasks);
        executor.shutdown(); // Executor 종료
        // 모든 스레드가 작업을 마칠 때까지 최대 1분 동안 기다림
        assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES));

        // then
        // 조회수는 500번의 요청에 의해 증가해야 하므로 500이어야 함
        assertEquals(500, post.getViews());
    }

    @Test
    @DisplayName("모든 포스팅 조회")
    void findAllPostsTest() {
        // given
        Post post1 = new Post("제목1", "내용1", PostVisibility.PUBLIC);
        Post post2 = new Post("제목2", "내용2", PostVisibility.PUBLIC);
        Post post3 = new Post("제목3", "내용3", PostVisibility.PUBLIC);

        post1.setUser(new User(1L));
        post2.setUser(new User(2L));
        post3.setUser(new User(3L));

        List<Post> postList = List.of(post1, post2, post3);

        Page<Post> postPage = new PageImpl<>(postList);

        given(postRepository.findAllPosts(pageable)).willReturn(postPage);

        // when
        Page<PostResponseDto> result = postService.findAllPosts(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("제목1");
        assertThat(result.getContent().get(1).getContent()).isEqualTo("내용2");
        assertThat(result.getContent().get(2).getTitle()).isEqualTo("제목3");

        verify(postRepository).findAllPosts(pageable);
    }

    @Test
    @DisplayName("한 포스팅의 댓글 전체조회")
    void findAllCommentsOfPostTest() {
        // given
        Long postId = 1L;
        Post post = new Post("제목", "내용", PostVisibility.PUBLIC);

        given(postRepository.findByIdOrElseThrow(postId)).willReturn(post);

        Comment comment1 = new Comment("댓글1");
        comment1.setUser(new User());

        Comment comment2 = new Comment("댓글2");
        comment2.setUser(new User());

        Comment comment3 = new Comment("댓글3");
        comment3.setUser(new User());

        List<Comment> commentList = List.of(comment1, comment2, comment3);

        Page<Comment> commentPage = new PageImpl<>(commentList);

        given(commentRepository.findAllCommentsWithPost(postId, pageable)).willReturn(commentPage);

        // when
        Page<PostCommentsResponseDto> result = postService.findAllCommentsOfPost(postId, null, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent().get(0).getComment()).isEqualTo("댓글1");
        assertThat(result.getContent().get(1).getComment()).isEqualTo("댓글2");
        assertThat(result.getContent().get(2).getComment()).isEqualTo("댓글3");

        verify(commentRepository).findAllCommentsWithPost(postId, pageable);
    }

    @Test
    @DisplayName("한 포스팅의 좋아요 누른 유저의 정보 조회")
    void findAllLikesUserDataTest() {
        // given
        Long postId = 1L;
        Post post = new Post("제목", "내용", PostVisibility.PUBLIC);

        given(postRepository.findByIdOrElseThrow(postId)).willReturn(post);

        User user1 = new User("닉네임1");
        User user2 = new User("닉네임2");
        User user3 = new User("닉네임3");
        User user4 = new User("닉네임4");

        post.setUser(user1);
        post.setUser(user2);
        post.setUser(user3);
        post.setUser(user4);

        List<User> userList = List.of(user1, user2, user3, user4);
        Page<User> userPage = new PageImpl<>(userList);

        given(postLikeRepository.findPostLikesByUserData(postId, pageable)).willReturn(userPage);

        // when
        Page<PostLikesUserResponseDto> result = postService.findAllLikesUserData(postId, null, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(4L);
        assertThat(result.getContent().get(0).getUserNickname()).isEqualTo("닉네임1");
        assertThat(result.getContent().get(2).getUserNickname()).isEqualTo("닉네임3");

        verify(postLikeRepository).findPostLikesByUserData(postId, pageable);
    }


}