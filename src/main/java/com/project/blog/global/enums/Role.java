package com.project.blog.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Role {
    USER("일반유저"),
    ADMIN("관리자");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    @JsonCreator // 요청받았을때, 반환해주는. from
    public static Role from(String name) {
        for (Role role : Role.values()) {
            if (role.getName().equalsIgnoreCase(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("잘못된 유저역할입니다: " + name);
    }

    @JsonValue
    public String getName() {
        return this.name;
    }

}
