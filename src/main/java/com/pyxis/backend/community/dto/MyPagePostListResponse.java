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
        LocalDateTime createdAt) {

    @QueryProjection
    public MyPagePostListResponse(
            Long postId,
            String title,
            String content,
            PostType postType,
            Long viewCount,
            LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.postType = postType;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
    }
}
