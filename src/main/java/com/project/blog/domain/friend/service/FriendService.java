package com.project.blog.domain.friend.service;

import com.project.blog.domain.friend.dto.FriendReceivedResponseDto;
import com.project.blog.domain.friend.dto.FriendSentResponseDto;
import com.project.blog.domain.friend.entity.Friend;
import com.project.blog.domain.friend.repository.FriendRepository;
import com.project.blog.domain.user.entity.User;
import com.project.blog.domain.user.repository.UserRepository;
import com.project.blog.global.enums.FriendStatus;
import com.project.blog.global.exception.business.CustomException;
import com.project.blog.global.exception.enums.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 친구 요청 및 친구 관리 기능을 제공하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    /**
     * 친구 요청을 보냅니다.
     *
     * @param senderId   친구 요청을 보내는 사용자의 ID
     * @param receiverId 친구 요청을 받는 사용자의 ID
     * @throws CustomException 자기 자신에게 요청하는 경우 또는 중복 요청 시 예외 발생
     */
    @Transactional
    public void sendFriend(Long senderId, Long receiverId) {
        // 내가 나에게 친구요청. 예외처리
        if (Objects.equals(senderId, receiverId)) {
            throw new CustomException(ErrorCode.FRIEND_BAD_REQUEST);
        }

        User sender = userRepository.findByIdOrElseThrow(senderId);
        User receiver = userRepository.findByIdOrElseThrow(receiverId);
        this.checkAlreadyFriend(senderId, receiverId);

        // Friend 객체 생성
        Friend friend = new Friend(FriendStatus.PENDING);
        friend.setSender(sender);
        friend.setReceiver(receiver);

        friendRepository.save(friend);
    }

    /**
     * 친구 요청을 수락합니다.
     *
     * @param senderId   친구 요청을 보낸 사용자의 ID
     * @param receiverId 친구 요청을 받은 사용자의 ID
     * @throws CustomException 친구 요청이 존재하지 않거나 자기 자신을 수락하려는 경우 예외 발생
     */
    @Transactional
    public void acceptFriend(Long senderId, Long receiverId) {
        if (Objects.equals(senderId, receiverId)) {
            throw new CustomException(ErrorCode.FRIEND_BAD_REQUEST);
        }

        Friend friend = friendRepository.findFriendBySenderIdAndReceiverId(senderId, receiverId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_FRIENDSHIP));

        friend.acceptFriendStatus(FriendStatus.ACCEPTED);
    }


    /**
     * 친구 요청을 거절하거나 친구 관계를 삭제합니다.
     *
     * @param senderId   친구 요청을 보낸 사용자의 ID
     * @param receiverId 친구 요청을 받은 사용자의 ID
     * @throws CustomException 친구 관계가 존재하지 않을 경우 예외 발생
     */
    @Transactional
    public void deleteFriend(Long senderId, Long receiverId) {
        Friend friend = friendRepository.findFriendBySenderIdAndReceiverId(senderId, receiverId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_FRIENDSHIP));

        friendRepository.delete(friend);
    }

    /**
     * 사용자가 보낸 친구 요청 목록을 조회합니다.
     *
     * @param loginUserId 현재 로그인한 사용자의 ID
     * @param pageable    페이징 정보
     * @return 보낸 친구 요청 목록을 담은 페이지 객체
     * @throws CustomException 사용자가 존재하지 않을 경우 예외 발생
     */
    public Page<FriendSentResponseDto> findPendingSentRequests(Long loginUserId, Pageable pageable) {
        if (!userRepository.existsById(loginUserId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        Page<Friend> friends = friendRepository.findBySenderId(loginUserId, pageable);

        return friends.map(FriendSentResponseDto::fromEntity);
    }

    /**
     * 사용자가 받은 친구 요청 목록을 조회합니다.
     *
     * @param loginUserId 현재 로그인한 사용자의 ID
     * @param pageable    페이징 정보
     * @return 받은 친구 요청 목록을 담은 페이지 객체
     * @throws CustomException 사용자가 존재하지 않을 경우 예외 발생
     */
    public Page<FriendReceivedResponseDto> findPendingReceivedRequests(Long loginUserId, Pageable pageable) {
        if (!userRepository.existsById(loginUserId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        Page<Friend> friends = friendRepository.findByReceiverId(loginUserId, pageable);

        return friends.map(FriendReceivedResponseDto::fromEntity);
    }

    private void checkAlreadyFriend(Long senderId, Long receiverId) {
        // 중복친구요청 확인
        if (friendRepository.existsBySenderIdAndReceiverId(senderId, receiverId) ||
                friendRepository.existsBySenderIdAndReceiverId(receiverId, senderId)
        ) {
            throw new CustomException(ErrorCode.ALREADY_FRIEND_REQUEST);
        }
    }
}
