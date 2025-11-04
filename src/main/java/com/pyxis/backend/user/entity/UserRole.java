package com.pyxis.backend.user.entity;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("사용자"),
    ADMIN("관리자");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    static UserRole fromString(String userRole) {
        for (UserRole role : UserRole.values()) {
            if (role.name().equalsIgnoreCase(userRole)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + userRole);
    }
}
