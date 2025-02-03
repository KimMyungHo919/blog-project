package com.project.blog.domain.friend.controller;

import com.project.blog.domain.friend.dto.FriendReceivedResponseDto;
import com.project.blog.domain.friend.dto.FriendSentResponseDto;
import com.project.blog.domain.friend.service.FriendService;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.constants.SessionAttributeKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;

    // 친구요청 보내기.
    @PostMapping("/{receiverId}")
    public ResponseEntity<String> sendFriend(
            @PathVariable Long receiverId,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        Long senderId = user.getId();

        friendService.sendFriend(senderId, receiverId);

        return ResponseEntity.status(HttpStatus.OK).body("친구요청을 보냈습니다.");
    }

    // 친구요청 수락.
    @PatchMapping("/request/{senderId}")
    public ResponseEntity<String> acceptFriend(
            @PathVariable Long senderId,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        Long receiverId = user.getId();

        friendService.acceptFriend(senderId, receiverId);

        return ResponseEntity.status(HttpStatus.OK).body("친구요청을 수락했습니다.");
    }

    // 친구요청 거절-삭제
    @DeleteMapping("/request/{senderId}")
    public ResponseEntity<String> deleteFriend(
            @PathVariable Long senderId,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        Long receiverId = user.getId();

        friendService.deleteFriend(senderId, receiverId);

        return ResponseEntity.status(HttpStatus.OK).body("친구삭제완료.");
    }

    // 친구요청 대기중 목록 조회 - 내가 보낸 친구요청
    @GetMapping("/pending/sent")
    public ResponseEntity<Page<FriendSentResponseDto>> findPendingSentRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size);

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        Page<FriendSentResponseDto> result = friendService.findPendingSentRequests(user.getId(), pageable);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 친구요청 대기중 목록 조회 - 내가 받은 친구요청
    @GetMapping("/pending/received")
    public ResponseEntity<Page<FriendReceivedResponseDto>> findPendingReceivedRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size);

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        Page<FriendReceivedResponseDto> result = friendService.findPendingReceivedRequests(user.getId(), pageable);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
