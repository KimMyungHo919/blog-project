package com.project.blog.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ImageType {
    PROFILE("프로필"),
    POST("포스팅");

    private final String value;

    ImageType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ImageType from(String value) {
        for (ImageType imageType : ImageType.values()) {
            if (imageType.value.equalsIgnoreCase(value)) {
                return imageType;
            }
        }
        throw new IllegalArgumentException("잘못된 이미지 타입입니다('프로필','포스팅' 입력가능): " + value);
    }

    @JsonValue // 응답할때
    public String getValue() {
        return this.value;
    }

    public static boolean isValid(String type) {
        for (ImageType imageType : ImageType.values()) {
            if (imageType.value.equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
}
