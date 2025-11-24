package com.pyxis.backend.community.dto;

import com.pyxis.backend.community.entity.PostType;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record MyPagePostListResponse(
        Long postId,
        String title,
        String content,
        PostType postType,
        Long viewCount,
        LocalDateTime createdAt,
        Long commentCount) {

    @QueryProjection
    public MyPagePostListResponse {
    }
}
