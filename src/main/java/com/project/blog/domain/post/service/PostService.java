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
import com.project.blog.domain.user.entity.User;
import com.project.blog.domain.user.repository.UserRepository;
import com.project.blog.global.exception.business.PostException;
import com.project.blog.global.exception.business.UserException;
import com.project.blog.global.exception.enums.ExceptionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    // 포스팅 작성
    @Transactional
    public PostResponseDto createPost(Long userId, PostRequestDto dto) {
        User user = userRepository.findByIdOrElseThrow(userId);

        Post post = new Post(
                dto.getTitle(),
                dto.getContent()
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
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    // 글 조회 -> 하나의 포스팅만 조회
    @Transactional
    public PostResponseDto findPost(Long postId) {
        Post post = postRepository.findByPostWithUserOrElseThrow(postId);
        long postLikesSize = postLikeRepository.sizeOfPost(postId);

        post.increaseViews();

        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViews(),
                postLikesSize,
                post.getUser().getNickname(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    // 글 조회 -> 모든 포스팅 조회
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
            throw new UserException(ExceptionType.USER_NOT_MATCH);
        }

        post.updateTitle(dto.getTitle());
        post.updateContent(dto.getContent());
    }

    // 글 삭제
    @Transactional
    public void deletePost(Long userId, Long postId) {
        boolean isExist = postRepository.existsByIdAndUserId(postId, userId);

        if (!isExist) {
            throw new UserException(ExceptionType.USER_NOT_MATCH);
        }

        postRepository.deleteById(postId);
    }

    // 한 포스팅의 댓글 전체조회
    public Page<PostCommentsResponseDto> findAllCommentsOfPost(Long postId, Pageable pageable) {
        if (!postRepository.existsById(postId)) {
            throw new PostException(ExceptionType.POST_NOT_FOUND);
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
    public Page<PostLikesUserResponseDto> findAllLikesUserData(Long postId, Pageable pageable) {
        if (!postRepository.existsById(postId)) {
            throw new PostException(ExceptionType.POST_NOT_FOUND);
        }

        Page<User> users = postLikeRepository.findPostLikesByUserData(postId, pageable);

        return users.map(
                user -> new PostLikesUserResponseDto(
                        user.getNickname()
                )
        );
    }
}
