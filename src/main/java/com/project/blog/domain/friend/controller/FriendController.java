package com.project.blog.domain.friend.controller;

import com.project.blog.domain.friend.dto.FriendReceivedResponseDto;
import com.project.blog.domain.friend.dto.FriendSentResponseDto;
import com.project.blog.domain.friend.service.FriendService;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.base.ApiResponse;
import com.project.blog.global.constants.SessionAttributeKeys;
import com.project.blog.global.exception.business.CustomException;
import com.project.blog.global.exception.enums.ExceptionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "친구 API", description = "친구 관련 API")
public class FriendController {

    private final FriendService friendService;

    // 친구요청 보내기.
    @PostMapping("/{receiverId}")
    @Operation(summary = "친구요청", description = "친구요청을 보냅니다.")
    public ResponseEntity<ApiResponse> sendFriend(
            @PathVariable Long receiverId,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        Long senderId = user.getId();

        friendService.sendFriend(senderId, receiverId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("친구요청을 보냈습니다"));
    }

    // 친구요청 수락.
    @PatchMapping("/request/{senderId}")
    @Operation(summary = "친구요청 수락", description = "친구요청을 수락합니다.")
    public ResponseEntity<ApiResponse> acceptFriend(
            @PathVariable Long senderId,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        Long receiverId = user.getId();

        friendService.acceptFriend(senderId, receiverId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("친구요청을 수락했습니다"));
    }

    // 친구요청 거절-삭제
    @DeleteMapping("/request/{senderId}")
    @Operation(summary = "친구요청 거절,삭제", description = "친구요청을 거절,삭제합니다.")
    public ResponseEntity<ApiResponse> deleteFriend(
            @PathVariable Long senderId,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        Long receiverId = user.getId();

        friendService.deleteFriend(senderId, receiverId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("친구삭제 완료"));
    }

    // 친구요청 대기중 목록 조회 - 내가 보낸 친구요청
    @GetMapping("/pending/sent")
    @Operation(summary = "보낸 친구요청 대기중 조회", description = "내가 보낸 친구요청 중 대기상태인 모든 친구를 조회합니다.")
    public ResponseEntity<ApiResponse> findPendingSentRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        this.pagingValidation(page, size);

        Pageable pageable = PageRequest.of(page, size);

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        Page<FriendSentResponseDto> friendSentResponseDto = friendService.findPendingSentRequests(user.getId(), pageable);

        ApiResponse result = ApiResponse.success(friendSentResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 친구요청 대기중 목록 조회 - 내가 받은 친구요청
    @GetMapping("/pending/received")
    @Operation(summary = "받은 친구요청 대기중 조회", description = "내가 받은 친구요청 중 대기상태인 모든 친구를 조회합니다.")
    public ResponseEntity<ApiResponse> findPendingReceivedRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        this.pagingValidation(page, size);

        Pageable pageable = PageRequest.of(page, size);

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionAttributeKeys.USER);

        Page<FriendReceivedResponseDto> friendReceivedResponseDto = friendService.findPendingReceivedRequests(user.getId(), pageable);

        ApiResponse result = ApiResponse.success(friendReceivedResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 페이징 유효성 검사
    private void pagingValidation(int page, int size) {
        if (page < 0) {
            throw new CustomException(ExceptionType.PAGE_BAD_REQUEST);
        }
        if (size < 1 || size > 20) {
            throw new CustomException(ExceptionType.PAGE_SIZE_BAD_REQUEST);
        }
    }

}
