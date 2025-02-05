package com.project.blog.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.blog.domain.comment.dto.request.CommentRequestDto;
import com.project.blog.domain.comment.dto.request.CommentUpdateRequestDto;
import com.project.blog.domain.comment.dto.response.CommentResponseDto;
import com.project.blog.domain.comment.service.CommentService;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.constants.SessionAttributeKeys;
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
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest {

    MockHttpSession session;
    User mockUser;
    Long postId;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CommentService commentService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @BeforeEach
    void setup() {
        postId = 1L;

        mockUser = new User(1L);
        session = new MockHttpSession();

        // 세션에 유저 저장해놓기
        session.setAttribute(SessionAttributeKeys.USER, mockUser);
    }

    @Test
    @DisplayName("댓글생성")
    void createCommentTest() throws Exception {
        CommentRequestDto dto = new CommentRequestDto("댓글1");
        CommentResponseDto responseDto = new CommentResponseDto(
                100L,
                "댓글1",
                "닉네임1",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        given(commentService.createComment(anyLong(), anyLong(), any(CommentRequestDto.class))).willReturn(responseDto);

        mockMvc.perform(post("/api/comments/post/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .session(session))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comment").value("댓글1"));

        verify(commentService).createComment(eq(postId), eq(mockUser.getId()), any(CommentRequestDto.class));
    }

    @Test
    @DisplayName("댓글수정")
    void updateCommentTest() throws Exception {
        Long commentId = 1L;
        CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto("바뀐댓글");

        mockMvc.perform(patch("/api/comments/{commentId}", commentId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("댓글 수정 완료."));

        verify(commentService).updateComment(anyLong(), anyLong(), any(CommentUpdateRequestDto.class));
    }

    @Test
    @DisplayName("댓글삭제")
    void deleteCommentTest() throws Exception {
        Long commentId = 1L;

        mockMvc.perform(delete("/api/comments/{commentId}", commentId)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("댓글 삭제 완료."));

        verify(commentService).deleteComment(anyLong(), anyLong());

    }
}