package com.pyxis.backend.community.dto;

import com.pyxis.backend.community.entity.CommPost;
import com.pyxis.backend.community.entity.PostType;
import com.pyxis.backend.user.entity.Users;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetCommPostResponse {

    private Long userId;
    private String nickname;
    private CommPostResponse community;

    @Builder
    @Getter
    public static class CommPostResponse {
        private String title;
        private String content;
        private PostType postType;
        private String imageUrl;
        private Long viewCount;
        private LocalDateTime createdAt;
    }

    public static GetCommPostResponse from(Users user, CommPost commPost) {
        return GetCommPostResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .community(
                        CommPostResponse.builder()
                                .title(commPost.getTitle())
                                .content(commPost.getContent())
                                .postType(commPost.getPostType())
                                .imageUrl(commPost.getImageURL())
                                .viewCount(commPost.getViewCount())
                                .createdAt(commPost.getCreatedAt())
                                .build()
                )
                .build();
    }
}
