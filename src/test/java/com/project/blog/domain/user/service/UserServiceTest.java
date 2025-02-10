package com.project.blog.domain.user.service;

import com.project.blog.domain.comment.entity.Comment;
import com.project.blog.domain.comment.repository.CommentRepository;
import com.project.blog.domain.friend.entity.Friend;
import com.project.blog.domain.friend.repository.FriendRepository;
import com.project.blog.domain.post.entity.Post;
import com.project.blog.domain.post.repository.PostRepository;
import com.project.blog.domain.postlike.repository.PostLikeRepository;
import com.project.blog.domain.user.dto.response.UserCommentResponseDto;
import com.project.blog.domain.user.dto.response.UserFriendsResponseDto;
import com.project.blog.domain.user.dto.response.UserPostLikeResponseDto;
import com.project.blog.domain.user.dto.response.UserPostsResponseDto;
import com.project.blog.domain.user.entity.User;
import com.project.blog.domain.user.repository.UserRepository;
import com.project.blog.global.enums.PostVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private Pageable pageable;
    private Long userId;

    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    PostLikeRepository postLikeRepository;
    @Mock
    FriendRepository friendRepository;

    @InjectMocks
    UserService userService;

    @BeforeEach
    void setup() {
        this.pageable = PageRequest.of(0, 10);
        this.userId = 1L;
    }

    @Test
    @DisplayName("한 유저의 포스트 전체조회")
    void findPostsByUser() {
        // given
        given(userRepository.existsById(userId)).willReturn(true);

        Post post1 = new Post("제목1", "내용1", PostVisibility.PUBLIC);
        post1.setUser(new User("테스터1"));
        Post post2 = new Post("제목2", "내용2", PostVisibility.PUBLIC);
        post2.setUser(new User("테스터2"));

        List<Post> postList = List.of(post1, post2);

        Page<Post> postPage = new PageImpl<>(postList);

        given(postRepository.findAllPostsWithUser(userId, pageable)).willReturn(postPage);

        // when
        Page<UserPostsResponseDto> result =
                userService.findPostsByUser(userId, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("제목1");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("제목2");
        assertThat(result.getContent().get(0).getContent()).isEqualTo("내용1");
        assertThat(result.getContent().get(1).getContent()).isEqualTo("내용2");
        assertThat(result.getContent().get(0).getUserNickname()).isEqualTo("테스터1");
        assertThat(result.getContent().get(1).getUserNickname()).isEqualTo("테스터2");

        verify(userRepository).existsById(userId);
        verify(postRepository).findAllPostsWithUser(userId, pageable);
    }

    @Test
    @DisplayName("한 유저의 댓글 전체조회")
    void findCommentsByUser() {
        // given
        given(userRepository.existsById(userId)).willReturn(true);

        Comment comment1 = new Comment("댓글1");
        comment1.setUser(new User("테스터1"));

        Comment comment2 = new Comment("댓글2");
        comment2.setUser(new User("테스터2"));

        List<Comment> commentList = List.of(comment1, comment2);

        Page<Comment> commentPage = new PageImpl<>(commentList);

        given(commentRepository.findAllCommentsWithUser(userId, pageable)).willReturn(commentPage);

        // when
        Page<UserCommentResponseDto> result =
                userService.findCommentsByUser(userId, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getComment()).isEqualTo("댓글1");
        assertThat(result.getContent().get(1).getComment()).isEqualTo("댓글2");
        assertThat(result.getContent().get(0).getUserNickname()).isEqualTo("테스터1");
        assertThat(result.getContent().get(1).getUserNickname()).isEqualTo("테스터2");

        verify(userRepository).existsById(userId);
        verify(commentRepository).findAllCommentsWithUser(userId, pageable);
    }

    @Test
    @DisplayName("한 유저의 좋아요 누른 게시물 조회")
    void findAllPostLikeTest() {
        // given
        given(userRepository.existsById(userId)).willReturn(true);

        Post post1 = new Post("제목1", "내용1", PostVisibility.PUBLIC);
        Post post2 = new Post("제목2", "내용2", PostVisibility.PUBLIC);

        List<Post> postList = List.of(post1, post2);

        Page<Post> postPage = new PageImpl<>(postList);

        given(postLikeRepository.findLikedPostsByUser(userId, pageable)).willReturn(postPage);

        // when
        Page<UserPostLikeResponseDto> result = userService.findAllPostLike(userId, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getPostTitle()).isEqualTo("제목1");
        assertThat(result.getContent().get(1).getPostTitle()).isEqualTo("제목2");
        assertThat(result.getContent().get(0).getPostContent()).isEqualTo("내용1");
        assertThat(result.getContent().get(1).getPostContent()).isEqualTo("내용2");

        verify(userRepository).existsById(userId);
        verify(postLikeRepository).findLikedPostsByUser(userId, pageable);
    }

    @Test
    @DisplayName("한 유저의 친구목록 조회")
    void findMyFriendsTest() {
        // given
        given(userRepository.existsById(userId)).willReturn(true);

        User user1 = new User(1L);
        User user2 = new User(2L);
        User user3 = new User(3L);

        Friend friend1 = new Friend(user1, user2);
        Friend friend2 = new Friend(user3, user1);

        List<Friend> friendList = List.of(friend1, friend2);

        Page<Friend> friendPage = new PageImpl<>(friendList);

        given(friendRepository.findMyFriends(userId, pageable)).willReturn(friendPage);

        // when
        Page<UserFriendsResponseDto> result = userService.findMyFriends(userId, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(2L);
        assertThat(result.getContent().get(1).getUserId()).isEqualTo(3L);

        verify(userRepository).existsById(userId);
        verify(friendRepository).findMyFriends(userId, pageable);
    }


}