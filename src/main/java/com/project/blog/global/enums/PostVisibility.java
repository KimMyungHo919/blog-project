package com.project.blog.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PostVisibility {
    PRIVATE("비공개"),
    PUBLIC("공개");

    private final String value;

    PostVisibility(String value) {
        this.value = value;
    }

    @JsonCreator // 요청받을때 JSON 의 문자열 값을 PostVisibility Enum 으로 자동 변환하는 역할
    public static PostVisibility from(String value) {
        for (PostVisibility visibility : PostVisibility.values()) {
            if (visibility.value.equalsIgnoreCase(value)) {
                return visibility;
            }
        }
        throw new IllegalArgumentException("잘못된 공개 상태 값입니다: " + value);
    }

    @JsonValue // 응답할때
    public String getValue() {
        return this.value;
    }
}
