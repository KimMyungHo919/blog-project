package com.project.blog.domain.post.service;

import com.project.blog.domain.post.dto.request.PostRequestDto;
import com.project.blog.domain.post.dto.response.PostResponseDto;
import com.project.blog.domain.post.entity.Post;
import com.project.blog.domain.post.repository.PostRepository;
import com.project.blog.domain.user.entity.User;
import com.project.blog.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

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
                post.getTitle(),
                post.getContent(),
                post.getUser().getNickname(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
