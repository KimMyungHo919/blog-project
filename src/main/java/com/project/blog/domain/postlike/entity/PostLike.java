package com.project.blog.domain.postlike.entity;

import com.project.blog.domain.post.entity.Post;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class PostLike extends BaseTimeEntity {
    /**
     * 컬럼 - 연관관계 컬럼을 제외한 컬럼을 정의합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /**
     * 생성자 - 약속된 형태로만 생성가능하도록 합니다.
     */
    public PostLike() {}


    /**
     * 연관관계 - Foreign Key 값을 따로 컬럼으로 정의하지 않고 연관 관계로 정의합니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 연관관계 편의 메소드 - 반대쪽에는 연관관계 편의 메소드가 없도록 주의합니다.
     */
    public void setPost(Post post) {
        this.post = post;
        post.addPostLikes(this);
    }

    public void setUser(User user) {
        this.user = user;
        user.addPostLikes(this);
    }

    /**
     * 서비스 메소드 - 외부에서 엔티티를 수정할 메소드를 정의합니다. (단일 책임을 가지도록 주의합니다.)
     */
}
