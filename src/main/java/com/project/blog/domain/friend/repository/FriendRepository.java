package com.project.blog.domain.friend.repository;

import com.project.blog.domain.friend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FriendRepository extends JpaRepository<Friend, Long> {

    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    boolean existsByReceiverId(Long receiverId);
}
