package com.project.blog.domain.friend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.blog.domain.friend.dto.FriendReceivedResponseDto;
import com.project.blog.domain.friend.dto.FriendSentResponseDto;
import com.project.blog.domain.friend.service.FriendService;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.constants.SessionAttributeKeys;
import com.project.blog.global.enums.FriendStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FriendController.class)
class FriendControllerTest {

    MockHttpSession session;
    User mockUser;
    Long receiverId;
    Pageable pageable = PageRequest.of(0, 10);

    @MockitoBean
    FriendService friendService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockUser = new User(1L);
        session = new MockHttpSession();
        session.setAttribute(SessionAttributeKeys.USER, mockUser);
        receiverId = 2L;
    }

    @Test
    @DisplayName("친구요청 보내기")
    void sendFriend() throws Exception {
        willDoNothing().given(friendService).sendFriend(mockUser.getId(), receiverId);

        mockMvc.perform(post("/api/friends/{receiverId}", receiverId)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("친구요청을 보냈습니다."));

        verify(friendService).sendFriend(mockUser.getId(), receiverId);
    }

    @Test
    @DisplayName("친구요청 수락")
    void acceptFriend() throws Exception {
        willDoNothing().given(friendService).acceptFriend(receiverId, mockUser.getId());

        mockMvc.perform(patch("/api/friends/request/{senderId}", receiverId)
                        .session(session))
                .andExpect(content().string("친구요청을 수락했습니다."));

        verify(friendService).acceptFriend(receiverId, mockUser.getId());
    }

    @Test
    @DisplayName("친구요청 거절-삭제")
    void deleteFriend() throws Exception {
        // 컨트롤러에 있는 receiverId 는 mockUser.getId()
        willDoNothing().given(friendService).deleteFriend(receiverId, mockUser.getId());

        mockMvc.perform(delete("/api/friends/request/{senderId}", receiverId)
                        .session(session))
                .andExpect(content().string("친구삭제완료."));

        verify(friendService).deleteFriend(receiverId, mockUser.getId());
    }

    @Test
    @DisplayName("친구요청 대기중 목록 조회 - 내가 보낸 친구요청")
    void findPendingSentRequests() throws Exception {
        FriendSentResponseDto friendSentResponseDto1 = new FriendSentResponseDto(10L, "abc1@naver.com", "n1", FriendStatus.PENDING);
        FriendSentResponseDto friendSentResponseDto2 = new FriendSentResponseDto(20L, "abc2@naver.com", "n2", FriendStatus.PENDING);

        List<FriendSentResponseDto> list = List.of(friendSentResponseDto1, friendSentResponseDto2);
        Page<FriendSentResponseDto> page = new PageImpl<>(list);

        given(friendService.findPendingSentRequests(mockUser.getId(), pageable)).willReturn(page);

        mockMvc.perform(get("/api/friends/pending/sent")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].userEmail").value("abc1@naver.com"))
                .andExpect(jsonPath("$.content[1].userEmail").value("abc2@naver.com"))
                .andExpect(jsonPath("$.content[0].userNickname").value("n1"))
                .andExpect(jsonPath("$.content[1].userNickname").value("n2"));

        verify(friendService).findPendingSentRequests(mockUser.getId(), pageable);
    }

    @Test
    @DisplayName("친구요청 대기중 목록 조회 - 내가 받은 친구요청")
    void findPendingReceivedRequests() throws Exception {
        FriendReceivedResponseDto dto1 = new FriendReceivedResponseDto(10L, "abc1@naver.com", "n1", FriendStatus.PENDING);
        FriendReceivedResponseDto dto2 = new FriendReceivedResponseDto(20L, "abc2@naver.com", "n2", FriendStatus.PENDING);

        List<FriendReceivedResponseDto> list = List.of(dto1, dto2);
        Page<FriendReceivedResponseDto> page = new PageImpl<>(list);

        given(friendService.findPendingReceivedRequests(mockUser.getId(), pageable)).willReturn(page);

        mockMvc.perform(get("/api/friends/pending/received")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].userEmail").value("abc1@naver.com"))
                .andExpect(jsonPath("$.content[1].userEmail").value("abc2@naver.com"))
                .andExpect(jsonPath("$.content[0].userNickname").value("n1"))
                .andExpect(jsonPath("$.content[1].userNickname").value("n2"));

        verify(friendService).findPendingReceivedRequests(mockUser.getId(), pageable);
    }
}