package com.project.blog.global.exception.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@JsonPropertyOrder({"status", "errorCode", "message"}) // 필드 순서를 지정
public class ErrorResponse {

    private String status;
    private String message;
    private String errorCode;

    public ErrorResponse(HttpStatus httpStatus, String message) {
        this.status = "error";
        this.errorCode = httpStatus.toString();
        this.message = message;
    }
}
