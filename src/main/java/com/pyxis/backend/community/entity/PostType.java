package com.pyxis.backend.community.entity;


import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import lombok.Getter;

import java.util.List;

@Getter
public enum PostType {

    DEFAULT("일반"),
    ANONYMOUS("익명");

    private final String description;

    PostType(String description) {
        this.description = description;
    }

    public static PostType fromString(String postType) {
        for (PostType type : PostType.values()) {
            if (type.name().equalsIgnoreCase(postType)) {
                return type;
            }
        }
        throw new CustomException(ErrorType.POST_TYPE_ERROR, List.of(postType));
    }
}
