package com.project.blog.global.encoder;

import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCrypt;


@Component
public class PasswordEncoder {

    // 비밀번호 암호화
    public String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(10)); // 10은 cost factor , 높을수록 암호화는 보안은 좋지만, 느려진다.
    }

    // 비밀번호 확인
    public boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
