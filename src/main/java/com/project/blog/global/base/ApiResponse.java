package com.project.blog.global.base;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse {

    private final int status;
    private final String message;
    private final Object data;

    public ApiResponse(HttpStatus status, String message, Object data) {
        this.status = status.value();
        this.message = message;
        this.data = data;
    }

    // 성공 응답
    public static ApiResponse success(Object data) {
        return new ApiResponse(HttpStatus.OK, HttpStatus.OK.name(), data);
    }

    public static ApiResponse created(Object data) {
        return new ApiResponse(HttpStatus.CREATED, HttpStatus.CREATED.name(), data);
    }

}
