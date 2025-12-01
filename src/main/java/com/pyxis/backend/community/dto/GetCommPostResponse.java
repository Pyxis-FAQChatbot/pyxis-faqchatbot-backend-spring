package com.pyxis.backend.community.dto;

import com.pyxis.backend.community.entity.PostType;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record GetCommPostResponse(
        Long userId,
        String nickname,
        CommPostResponse community,
        long commentCount
) {

    public record CommPostResponse(String title, String content, PostType postType, String imageUrl, Long viewCount,
                                   LocalDateTime createdAt) {
    }

    @QueryProjection
    public GetCommPostResponse(Long userId, String nickname,
                               String title, String content, PostType postType,
                               String imageUrl, Long viewCount,
                               LocalDateTime createdAt, long commentCount) {

        this(
                userId,
                nickname,
                new CommPostResponse(title, content, postType, imageUrl, viewCount, createdAt),
                commentCount
        );
    }
}

