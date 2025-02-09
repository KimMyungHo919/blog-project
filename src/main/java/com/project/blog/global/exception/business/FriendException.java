package com.project.blog.global.exception.business;

import com.project.blog.global.exception.enums.ExceptionType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FriendException extends RuntimeException {

    private final HttpStatus httpStatus;

    public FriendException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.httpStatus = exceptionType.getHttpStatus();
    }
}
