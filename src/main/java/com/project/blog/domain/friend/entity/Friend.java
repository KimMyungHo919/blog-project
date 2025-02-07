package com.project.blog.domain.friend.entity;

import com.project.blog.domain.user.entity.User;
import com.project.blog.global.base.BaseTimeEntity;
import com.project.blog.global.enums.FriendStatus;
import com.project.blog.global.exception.CustomException;
import com.project.blog.global.exception.ExceptionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Entity
@Getter
@AllArgsConstructor
public class Friend extends BaseTimeEntity {

    /**
     * 컬럼 - 연관관계 컬럼을 제외한 컬럼을 정의합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private FriendStatus friendStatus;

    /**
     * 생성자 - 약속된 형태로만 생성가능하도록 합니다.
     */
    public Friend() {}

    public Friend(FriendStatus friendStatus) {
        this.friendStatus = friendStatus;
    }

    // 테스트용 생성자
    public Friend(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }


    /**
     * 연관관계 - Foreign Key 값을 따로 컬럼으로 정의하지 않고 연관 관계로 정의합니다.
     */
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;


    /**
     * 연관관계 편의 메소드 - 반대쪽에는 연관관계 편의 메소드가 없도록 주의합니다.
     */
    public void setSender(User sender) {
        this.sender = sender;
        sender.addSenders(this);
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
        receiver.addReceivers(this);
    }


    /**
     * 서비스 메소드 - 외부에서 엔티티를 수정할 메소드를 정의합니다. (단일 책임을 가지도록 주의합니다.)
     */
    public void acceptFriendStatus(FriendStatus friendStatus) {
        if (Objects.equals(this.friendStatus, FriendStatus.ACCEPTED)) {
            throw new CustomException(ExceptionType.ALREADY_FRIEND);
        }

        this.friendStatus = friendStatus;
    }

}
