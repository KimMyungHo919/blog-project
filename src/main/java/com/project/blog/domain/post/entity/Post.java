package com.project.blog.domain.post.entity;

import com.project.blog.domain.comment.entity.Comment;
import com.project.blog.domain.postlike.entity.PostLike;
import com.project.blog.domain.postview.entity.PostView;
import com.project.blog.domain.user.entity.User;
import com.project.blog.global.base.BaseTimeEntity;
import com.project.blog.global.enums.PostVisibility;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Post extends BaseTimeEntity {

    /**
     * 컬럼 - 연관관계 컬럼을 제외한 컬럼을 정의합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private int views = 0;

    @Enumerated(EnumType.STRING) // 공개여부
    private PostVisibility postVisibility;

    /**
     * 생성자 - 약속된 형태로만 생성가능하도록 합니다.
     */
    public Post() {}

    public Post(String title, String content, PostVisibility postVisibility) {
        this.title = title;
        this.content = content;
        this.postVisibility = postVisibility;
    }

    // 테스트용 생성
    public Post(Long id, int views, PostVisibility postVisibility) {
        this.id = id;
        this.views = views;
        this.postVisibility = postVisibility;
    }

    /**
     * 연관관계 - Foreign Key 값을 따로 컬럼으로 정의하지 않고 연관 관계로 정의합니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostView> postViews = new ArrayList<>();

    /**
     * 연관관계 편의 메소드 - 반대쪽에는 연관관계 편의 메소드가 없도록 주의합니다.
     */
    public void setUser(User user) {
        this.user = user;
        user.addPosts(this);
    }


    /**
     * 서비스 메소드 - 외부에서 엔티티를 수정할 메소드를 정의합니다. (단일 책임을 가지도록 주의합니다.)
     */
    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void addPostLikes(PostLike postLike) {
        this.postLikes.add(postLike);
    }

    public void addPostViews(PostView postView) {
        this.postViews.add(postView);
    }

    public void increaseViews() {
        this.views += 1;
    }

    public void changeIsVisibility(PostVisibility postVisibility) {
        this.postVisibility = postVisibility;
    }

}
