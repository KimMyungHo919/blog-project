package com.project.blog.domain.friend.repository;

import com.project.blog.domain.friend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface FriendRepository extends JpaRepository<Friend, Long> {

    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    Optional<Friend> findFriendBySenderIdAndReceiverId(Long senderId, Long receiverId);
}
