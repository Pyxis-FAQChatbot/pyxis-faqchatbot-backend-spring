package com.pyxis.backend.comment.entity;

import lombok.Getter;


@Getter
public enum CommentStatus {
    ACTIVE("정상"),
    DELETED("삭제"),
    BLOCKED("가림");

    private final String description;

    CommentStatus(String description) {
        this.description = description;
    }

}
