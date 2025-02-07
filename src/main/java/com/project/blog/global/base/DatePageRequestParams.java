package com.project.blog.global.base;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatePageRequestParams {

    @Min(value = 0, message = "페이지는 0보다 작을수 없습니다.")
    private int page = 0;

    @Min(value = 1, message = "size 는 최소 1 입니다.")
    @Max(value = 10, message = "size 는 최대 10까지 입력가능합니다.")
    private int size = 10;

    @Pattern(regexp = "createdAt|updatedAt", message = "입력값이 잘못되었습니다")
    private String sortBy = "createdAt";

    @Pattern(regexp = "asc|desc", message = "asc 혹은 desc 만 요청 가능합니다.")
    private String direction = "desc";
}
