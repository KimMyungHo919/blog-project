package com.project.blog.domain.postlike.controller;

import com.project.blog.domain.postlike.service.PostLikeService;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.constants.SessionAttributeKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willDoNothing;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostLikeController.class)
class PostLikeControllerTest {

    MockHttpSession session;
    User user = new User(1L);

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockitoBean
    PostLikeService postLikeService;

    @BeforeEach
    void setup() {
        session = new MockHttpSession();
        session.setAttribute(SessionAttributeKeys.USER, user);
    }

    @Test
    @DisplayName("좋아요 누르기")
    void addPostLike() throws Exception {

        Long postId = 100L;

        willDoNothing().given(postLikeService).addPostLike(postId, user.getId());

        mockMvc.perform(post("/api/likes/post/{postId}", postId)
                        .session(session))
                .andExpect(status().isCreated())
                .andExpect(content().string("좋아요!"));
    }

    @Test
    @DisplayName("좋아요 취소")
    void cancelPostLike() throws Exception {
        Long postId = 100L;

        willDoNothing().given(postLikeService).addPostLike(postId, user.getId());

        mockMvc.perform(delete("/api/likes/post/{postId}", postId)
                        .session(session))
                .andExpect(status().isCreated())
                .andExpect(content().string("좋아요 취소."));
    }
}