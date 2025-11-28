package com.pyxis.backend.user.entity;


import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import lombok.Getter;

import java.util.List;

@Getter
public enum UserSocialType {
    NONE("기본"),
    KAKAO("카카오"),
    NAVER("네이버");

    private final String description;

    UserSocialType(String description) {
        this.description = description;
    }

    public static UserSocialType fromString(String userSocialType) {
        for (UserSocialType type : UserSocialType.values()) {
            if (type.name().equalsIgnoreCase(userSocialType)) {
                return type;
            }
        }
        throw new CustomException(ErrorType.GENDER_TYPE_ERROR, List.of(userSocialType));
    }
}
