package com.pyxis.backend.community.dto;

import com.pyxis.backend.community.entity.CommPost;
import com.pyxis.backend.community.entity.PostType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommPostListResponse {

    private Long communityId;
    private String nickname;
    private PostType postType;
    private String title;
    private String content;
    private Long viewCount;
    private LocalDateTime createdAt;

    public static CommPostListResponse of(CommPost commPost) {
        return CommPostListResponse.builder()
                .communityId(commPost.getId())
                .nickname(commPost.getUser().getNickname())
                .postType(commPost.getPostType())
                .title(commPost.getTitle())
                .content(commPost.getContent())
                .viewCount(commPost.getViewCount())
                .createdAt(commPost.getCreatedAt())
                .build();
    }
}
