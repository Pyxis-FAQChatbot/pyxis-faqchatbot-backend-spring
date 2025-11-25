package com.pyxis.backend.community.dto;

import com.pyxis.backend.community.entity.PostType;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record CommPostListResponse(Long postId,
                                   String title,
                                   String content,
                                   Long userId,
                                   String nickname,
                                   PostType postType,
                                   Long viewCount,
                                   LocalDateTime createdAt,
                                   Long commentCount) {

    @QueryProjection
    public CommPostListResponse {
    }
}

