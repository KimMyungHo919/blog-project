package com.project.blog.domain.user.entity;

import com.project.blog.domain.user.model.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {

    /**
     * 컬럼 - 연관관계 컬럼을 제외한 컬럼을 정의합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String nickName;

    private String password;

    private Role role;

    /**
     * 생성자 - 약속된 형태로만 생성가능하도록 합니다.
     */
    public User() {}

    public User(String email, String nickName, String password, Role role) {
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.role = role;
    }

    /**
     * 연관관계 - Foreign Key 값을 따로 컬럼으로 정의하지 않고 연관 관계로 정의합니다.
     */


    /**
     * 연관관계 편의 메소드 - 반대쪽에는 연관관계 편의 메소드가 없도록 주의합니다.
     */


    /**
     * 서비스 메소드 - 외부에서 엔티티를 수정할 메소드를 정의합니다. (단일 책임을 가지도록 주의합니다.)
     */
    public void changePassword(String password) {
        this.password = password;
    }

}
