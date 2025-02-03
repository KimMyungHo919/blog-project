package com.project.blog.domain.friend.repository;

import com.project.blog.domain.friend.entity.Friend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface FriendRepository extends JpaRepository<Friend, Long> {

    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    Optional<Friend> findFriendBySenderIdAndReceiverId(Long senderId, Long receiverId);

    @Query(value = "SELECT f FROM Friend f " +
            "JOIN FETCH f.sender " +
            "JOIN FETCH f.receiver " +
            "WHERE (f.receiver.id = :id OR f.sender.id = :id) " +
            "AND f.friendStatus = 'ACCEPTED'",
            countQuery = "SELECT COUNT(f) FROM Friend f " +
                    "WHERE (f.receiver.id = :id OR f.sender.id = :id) " +
                    "AND f.friendStatus = 'ACCEPTED'")
    Page<Friend> findMyFriends(Long id, Pageable pageable);

    @Query("SELECT f FROM Friend f " +
            "JOIN FETCH f.sender " +
            "JOIN FETCH f.receiver " +
            "WHERE f.sender.id = :loginUserId " +
            "AND f.friendStatus = 'PENDING'")
    Page<Friend> findBySenderId(Long loginUserId, Pageable pageable);

    @Query("SELECT f FROM Friend f " +
            "JOIN FETCH f.receiver " +
            "JOIN FETCH f.sender " +
            "WHERE f.receiver.id = :loginUserId " +
            "AND f.friendStatus = 'PENDING'")
    Page<Friend> findByReceiverId(Long loginUserId, Pageable pageable);
}
