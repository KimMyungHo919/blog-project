package com.project.blog.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.project.blog.global.exception.business.CustomException;
import com.project.blog.global.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public enum PostCategory {
    LIFESTYLE("일상"),
    TRAVEL("여행"),
    FOOD("음식"),
    FASHION("패션"),
    HEALTH("건강"),

    ENTERTAINMENT("엔터테인먼트"),
    MOVIES("영화"),
    MUSIC("음악"),
    GAME("게임"),
    BOOKS("책"),

    SPORTS("스포츠"),

    CULTURE("문화"),
    SOCIETY("사회"),
    BUSINESS("비지니스");


    private final String value;

    PostCategory(String value) {
        this.value = value;
    }

    @JsonCreator
    public static PostCategory from(String value) {
        for (PostCategory visibility : PostCategory.values()) {
            if (visibility.value.equalsIgnoreCase(value)) {
                return visibility;
            }
        }
        throw new CustomException(ErrorCode.INVALID_CATEGORY);
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
