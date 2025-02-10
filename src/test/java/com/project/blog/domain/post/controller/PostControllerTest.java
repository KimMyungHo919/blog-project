package com.project.blog.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.blog.domain.post.dto.request.PostRequestDto;
import com.project.blog.domain.post.dto.response.PostResponseDto;
import com.project.blog.domain.post.service.PostService;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.constants.SessionAttributeKeys;
import com.project.blog.global.enums.PostVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostController.class)
class PostControllerTest {

    MockHttpSession session;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    PostService postService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @BeforeEach
    void setup() {
        session = new MockHttpSession(); // 테스트메소드 시작 전 세션 초기화
    }

    @Test
    @DisplayName("포스팅 작성")
    void createPostTest() throws Exception {
        // given
        User mockUser = new User(1L);
        session.setAttribute(SessionAttributeKeys.USER, mockUser);

        PostRequestDto requestDto = new PostRequestDto("제목은다섯글자가넘어야합니다", "내용은길어야합니다더더더더", PostVisibility.PUBLIC);

        PostResponseDto responseDto = new PostResponseDto(
                1L,
                "제목은다섯글자가넘어야합니다",
                "내용은길어야합니다더더더더",
                10,
                10,
                "유저이름1",
                PostVisibility.PUBLIC.getValue(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        given(postService.createPost(anyLong(), any(PostRequestDto.class))).willReturn(responseDto);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .session(session))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.postId").value(1L))
                .andExpect(jsonPath("$.data.title").value("제목은다섯글자가넘어야합니다"))
                .andExpect(jsonPath("$.data.content").value("내용은길어야합니다더더더더"));
    }

    @Test
    @DisplayName("하나의 포스팅만 조회")
    void findPostTest() throws Exception {
        Long postId = 1L;

        PostResponseDto responseDto = new PostResponseDto(
                postId,
                "제목은다섯글자가넘어야합니다",
                "내용은길어야합니다더더더더",
                10,
                10,
                "유저이름1",
                PostVisibility.PUBLIC.getValue(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        given(postService.findPost(postId, null)).willReturn(responseDto);

        mockMvc.perform(get("/api/public/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("제목은다섯글자가넘어야합니다"))
                .andExpect(jsonPath("$.data.content").value("내용은길어야합니다더더더더"))
                .andExpect(jsonPath("$.data.userNickname").value("유저이름1"));
    }

}