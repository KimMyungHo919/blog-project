package com.project.blog.domain.user.entity;

import com.project.blog.domain.comment.entity.Comment;
import com.project.blog.domain.friend.entity.Friend;
import com.project.blog.domain.post.entity.Post;
import com.project.blog.domain.postlike.entity.PostLike;
import com.project.blog.global.base.BaseTimeEntity;
import com.project.blog.global.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class User extends BaseTimeEntity {

    /**
     * 컬럼 - 연관관계 컬럼을 제외한 컬럼을 정의합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isVerified; // 이메일 인증 여부

    private String verificationToken; // 이메일 인증 토큰

    private LocalDateTime tokenExpiryTime; // 토큰 만료 시간

    private Long imageId; // 프로필 이미지 아이디

    private String profileImageUrl; // 프로필 이미지


    /**
     * 생성자 - 약속된 형태로만 생성가능하도록 합니다.
     */
    public User() {}

    public User(String email, String password, String nickname, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    // 테스트용 생성자
    public User(String nickname) {
        this.nickname = nickname;
    }

    // 테스트용 생성자
    public User(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    // 테스트용 생성자
    public User(Long id) {
        this.id = id;
    }

    /**
     * 연관관계 - Foreign Key 값을 따로 컬럼으로 정의하지 않고 연관 관계로 정의합니다.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friend> senders = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friend> receivers = new ArrayList<>();


    /**
     * 연관관계 편의 메소드 - 반대쪽에는 연관관계 편의 메소드가 없도록 주의합니다.
     */


    /**
     * 서비스 메소드 - 외부에서 엔티티를 수정할 메소드를 정의합니다. (단일 책임을 가지도록 주의합니다.)
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void addPosts(Post post) {
        this.posts.add(post);
    }

    public void addComments(Comment comment) {
        this.comments.add(comment);
    }

    public void addPostLikes(PostLike postLike) {
        this.postLikes.add(postLike);
    }

    public void addSenders(Friend friend) {
        this.senders.add(friend);
    }

    public void addReceivers(Friend friend) {
        this.receivers.add(friend);
    }

    public void setVerified(boolean verified) {
        this.isVerified = verified;
    }

    public void setVerificationToken(String token) {
        this.verificationToken = token;
    }

    public void setTokenExpiryTime(LocalDateTime time) {
        this.tokenExpiryTime = time;
    }

    public void setProfile(Long imageId, String profileImage) { // 프로필 세팅
        this.imageId = imageId;
        this.profileImageUrl = profileImage;
    }

}
