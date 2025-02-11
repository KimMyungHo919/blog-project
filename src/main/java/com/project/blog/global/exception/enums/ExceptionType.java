package com.project.blog.global.exception.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {
    // USER 관련
    ALREADY_LOGIN(HttpStatus.BAD_REQUEST, "이미 로그인한 사용자입니다."),
    EXIST_USER(HttpStatus.BAD_REQUEST, "동일한 email 의 사용자가 존재합니다."),
    PASSWORD_NOT_CORRECT(HttpStatus.BAD_REQUEST,  "비밀번호가 일치하지 않습니다."),
    EMAIL_NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "이메일 인증을 완료해주세요."),
    PASSWORD_SAME(HttpStatus.BAD_REQUEST, "기존의 비밀번호와 일치합니다."),
    ALREADY_SAME_NICKNAME(HttpStatus.BAD_REQUEST,  "현재 닉네임과 같은 닉네임입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,  "해당 유저의 정보를 찾을 수 없습니다."),
    USER_NOT_MATCH(HttpStatus.BAD_REQUEST, "잘못된 유저의 정보에 접근하고 있습니다."),

    // POST 관련
    POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 게시물을 찾을 수 없습니다."),
    PRIVATE_POST(HttpStatus.BAD_REQUEST, "비밀글입니다."),

    // PAGE 관련
    PAGE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "페이지 번호는 0 이상이어야 합니다."),
    PAGE_SIZE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "size 는 최소 1, 최대 20 사이여야 합니다."),

    // POST-LIKE 관련
    ALREADY_POST_LIKE(HttpStatus.BAD_REQUEST, "이미 좋아요를 누른 게시물입니다."),
    NOTFOUND_POST_LIKE(HttpStatus.BAD_REQUEST, "좋아요를 누른적이 없는 게시물입니다."),

    // FRIEND 관련
    FRIEND_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    ALREADY_FRIEND_REQUEST(HttpStatus.NOT_FOUND, "이미 친구요청이 존재합니다."),
    NOT_FOUND_FRIENDSHIP(HttpStatus.NOT_FOUND, "존재하지않는 친구요청입니다."),
    ALREADY_FRIEND(HttpStatus.NOT_FOUND, "이미 친구관계입니다."),

    // COMMENT 관련
    COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 댓글을 찾을 수 없습니다."),

    LOCK_ACQUISITION_FAILED(HttpStatus.LOCKED, "락획득 실패");

    private final HttpStatus httpStatus;
    private final String message;

    ExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
