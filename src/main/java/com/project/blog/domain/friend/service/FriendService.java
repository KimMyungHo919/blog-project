package com.project.blog.domain.friend.service;

import com.project.blog.domain.friend.dto.FriendReceivedResponseDto;
import com.project.blog.domain.friend.dto.FriendSentResponseDto;
import com.project.blog.domain.friend.entity.Friend;
import com.project.blog.domain.friend.repository.FriendRepository;
import com.project.blog.domain.user.entity.User;
import com.project.blog.domain.user.repository.UserRepository;
import com.project.blog.global.enums.FriendStatus;
import com.project.blog.global.exception.business.FriendException;
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
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    // 친구요청 보내기.
    @Transactional
    public void sendFriend(Long senderId, Long receiverId) {
        // 내가 나에게 친구요청. 예외처리
        if (Objects.equals(senderId, receiverId)) {
            throw new FriendException(ExceptionType.FRIEND_BAD_REQUEST);
        }

        User sender = userRepository.findByIdOrElseThrow(senderId);
        User receiver = userRepository.findByIdOrElseThrow(receiverId);

        // 중복친구요청 확인
        if (friendRepository.existsBySenderIdAndReceiverId(senderId, receiverId) ||
                friendRepository.existsBySenderIdAndReceiverId(receiverId, senderId)
        ) {
            throw new FriendException(ExceptionType.ALREADY_FRIEND_REQUEST);
        }

        // Friend 객체 생성
        Friend friend = new Friend(FriendStatus.PENDING);
        friend.setSender(sender);
        friend.setReceiver(receiver);

        friendRepository.save(friend);
    }

    // 친구요청 수락
    @Transactional
    public void acceptFriend(Long senderId, Long receiverId) {
        if (Objects.equals(senderId, receiverId)) {
            throw new FriendException(ExceptionType.FRIEND_BAD_REQUEST);
        }

        Friend friend = friendRepository.findFriendBySenderIdAndReceiverId(senderId, receiverId)
                .orElseThrow(() -> new FriendException(ExceptionType.NOT_FOUND_FRIENDSHIP));

        friend.acceptFriendStatus(FriendStatus.ACCEPTED);
    }

    // 친구요청 거절-삭제
    @Transactional
    public void deleteFriend(Long senderId, Long receiverId) {
        Friend friend = friendRepository.findFriendBySenderIdAndReceiverId(senderId, receiverId)
                .orElseThrow(() -> new FriendException(ExceptionType.NOT_FOUND_FRIENDSHIP));

        friendRepository.delete(friend);
    }

    // 친구요청 대기중 목록 조회 - 내가보낸 친구요청
    public Page<FriendSentResponseDto> findPendingSentRequests(Long loginUserId, Pageable pageable) {
        if (!userRepository.existsById(loginUserId)) {
            throw new UserException(ExceptionType.USER_NOT_FOUND);
        }

        Page<Friend> friends = friendRepository.findBySenderId(loginUserId, pageable);

        return friends.map(
                friend -> new FriendSentResponseDto(
                        friend.getReceiver().getId(),
                        friend.getReceiver().getEmail(),
                        friend.getReceiver().getNickname(),
                        friend.getFriendStatus()
                )
        );
    }

    // 친구요청 대기중 목록 조회 - 내가 받은 친구요청
    public Page<FriendReceivedResponseDto> findPendingReceivedRequests(Long loginUserId, Pageable pageable) {
        if (!userRepository.existsById(loginUserId)) {
            throw new UserException(ExceptionType.USER_NOT_FOUND);
        }

        Page<Friend> friends = friendRepository.findByReceiverId(loginUserId, pageable);

        return friends.map(
                friend -> new FriendReceivedResponseDto(
                        friend.getSender().getId(),
                        friend.getSender().getEmail(),
                        friend.getSender().getNickname(),
                        friend.getFriendStatus()
                )
        );
    }
}
