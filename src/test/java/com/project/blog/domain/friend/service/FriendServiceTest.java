package com.project.blog.domain.friend.service;

import com.project.blog.domain.friend.dto.FriendReceivedResponseDto;
import com.project.blog.domain.friend.dto.FriendSentResponseDto;
import com.project.blog.domain.friend.entity.Friend;
import com.project.blog.domain.friend.repository.FriendRepository;
import com.project.blog.domain.user.entity.User;
import com.project.blog.domain.user.repository.UserRepository;
import com.project.blog.global.enums.FriendStatus;
import com.project.blog.global.exception.business.CustomException;
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
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    Pageable pageable;

    @Mock
    FriendRepository friendRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    FriendService friendService;

    @BeforeEach
    void setup() {
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("친구요청 보내기 - 정상작동")
    void sendFriend() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;

        User sender = new User(senderId);
        User receiver = new User(receiverId);
        Friend friend = new Friend(FriendStatus.PENDING);
        friend.setSender(sender);
        friend.setReceiver(receiver);

        given(userRepository.findByIdOrElseThrow(senderId)).willReturn(sender);
        given(userRepository.findByIdOrElseThrow(receiverId)).willReturn(receiver);
        given(friendRepository.existsBySenderIdAndReceiverId(anyLong(), anyLong())).willReturn(false);
        given(friendRepository.save(any(Friend.class))).willReturn(friend);

        // when
        friendService.sendFriend(senderId, receiverId);

        // then
        verify(friendRepository).save(any(Friend.class));
    }

    @Test
    @DisplayName("자기 자신에게 친구 요청하면 예외 발생")
    void sendFriend_SelfRequest_ShouldThrowException() {
        // given
        Long senderId = 1L;
        Long receiverId = 1L;

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> friendService.sendFriend(senderId, receiverId));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    @DisplayName("이미 친구 요청을 보냈거나 받은 경우 예외 발생")
    void sendFriend_AlreadyRequested_ShouldThrowException() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;
        User sender = new User(senderId);
        User receiver = new User(receiverId);

        given(userRepository.findByIdOrElseThrow(senderId)).willReturn(sender);
        given(userRepository.findByIdOrElseThrow(receiverId)).willReturn(receiver);
        given(friendRepository.existsBySenderIdAndReceiverId(senderId, receiverId)).willReturn(true);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> friendService.sendFriend(senderId, receiverId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    @DisplayName("친구요청 수락")
    void acceptFriend() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;
        User sender = new User(senderId);
        User receiver = new User(receiverId);

        Friend friend = new Friend(FriendStatus.PENDING);
        friend.setSender(sender);
        friend.setReceiver(receiver);

        given(friendRepository.findFriendBySenderIdAndReceiverId(anyLong(), anyLong())).willReturn(Optional.of(friend));

        // when
        friendService.acceptFriend(senderId, receiverId);

        // then
        assertEquals(FriendStatus.ACCEPTED, friend.getFriendStatus());
    }

    @Test
    @DisplayName("친구요청 거절-삭제")
    void deleteFriend() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;
        User sender = new User(senderId);
        User receiver = new User(receiverId);

        Friend friend = new Friend(FriendStatus.PENDING);
        friend.setSender(sender);
        friend.setReceiver(receiver);

        given(friendRepository.findFriendBySenderIdAndReceiverId(anyLong(), anyLong())).willReturn(Optional.of(friend));
        willDoNothing().given(friendRepository).delete(friend);

        // when
        friendService.deleteFriend(senderId, receiverId);

        // then
        verify(friendRepository).delete(friend);
    }

    @Test
    @DisplayName("친구요청 대기중 목록 조회 - 내가 보낸 친구요청")
    void findPendingSentRequests() {
        // given
        Long loginUserId = 1L;

        given(userRepository.existsById(loginUserId)).willReturn(true);

        User sender = new User(loginUserId);
        User receiver1 = new User(2L);
        User receiver2 = new User(3L);
        User receiver3 = new User(4L);

        Friend friend1 = new Friend(FriendStatus.PENDING);
        friend1.setSender(sender);
        friend1.setReceiver(receiver1);

        Friend friend2 = new Friend(FriendStatus.PENDING);
        friend2.setSender(sender);
        friend2.setReceiver(receiver2);

        Friend friend3 = new Friend(FriendStatus.PENDING);
        friend3.setSender(sender);
        friend3.setReceiver(receiver3);

        List<Friend> friendList = List.of(friend1, friend2, friend3);
        Page<Friend> friendPage = new PageImpl<>(friendList);

        given(friendRepository.findBySenderId(loginUserId, pageable)).willReturn(friendPage);

        // when
        Page<FriendSentResponseDto> result = friendService.findPendingSentRequests(loginUserId, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(2L);
        assertThat(result.getContent().get(1).getUserId()).isEqualTo(3L);
        assertThat(result.getContent().get(2).getUserId()).isEqualTo(4L);
    }

    @Test
    @DisplayName("친구요청 보내기 - 내가 받은 친구요청")
    void findPendingReceivedRequests() {
        // given
        Long loginUserId = 1L;

        given(userRepository.existsById(loginUserId)).willReturn(true);

        User sender = new User(loginUserId);
        User receiver1 = new User(2L);
        User receiver2 = new User(3L);
        User receiver3 = new User(4L);

        Friend friend1 = new Friend(FriendStatus.PENDING);
        friend1.setSender(receiver1);
        friend1.setReceiver(sender);

        Friend friend2 = new Friend(FriendStatus.PENDING);
        friend2.setSender(receiver2);
        friend2.setReceiver(sender);

        Friend friend3 = new Friend(FriendStatus.PENDING);
        friend3.setSender(receiver3);
        friend3.setReceiver(sender);

        List<Friend> friendList = List.of(friend1, friend2, friend3);
        Page<Friend> friendPage = new PageImpl<>(friendList);

        given(friendRepository.findByReceiverId(loginUserId, pageable)).willReturn(friendPage);

        // when
        Page<FriendReceivedResponseDto> result = friendService.findPendingReceivedRequests(loginUserId, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(2L);
        assertThat(result.getContent().get(1).getUserId()).isEqualTo(3L);
        assertThat(result.getContent().get(2).getUserId()).isEqualTo(4L);
    }
}