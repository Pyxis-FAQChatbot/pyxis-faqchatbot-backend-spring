package com.pyxis.backend.user.entity;

import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import lombok.Getter;

import java.util.List;

@Getter
public enum GenderType {

    MALE("남성"),
    FEMALE("여성");

    private final String description;

    GenderType(String description) {
        this.description = description;
    }

    public static GenderType fromString(String genderType) {
        for (GenderType type : GenderType.values()) {
            if (type.name().equalsIgnoreCase(genderType)) {
                return type;
            }
        }
        throw new CustomException(ErrorType.GENDER_TYPE_ERROR, List.of(genderType));
    }
}
