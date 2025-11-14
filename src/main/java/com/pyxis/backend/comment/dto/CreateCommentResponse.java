package com.pyxis.backend.comment.dto;

import com.pyxis.backend.comment.entity.Comment;
import com.pyxis.backend.user.dto.SessionUser;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CreateCommentResponse {

    private Long commentId;
    private Long parentCommentId;
    private Long userId;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;

    public static CreateCommentResponse of(Comment comment, SessionUser sessionUser) {
        Long parentId =
                comment.getParentComment() != null
                        ? comment.getParentComment().getId()
                        : null;

        return CreateCommentResponse.builder()
                .commentId(comment.getId())
                .parentCommentId(parentId)
                .userId(sessionUser.getId())
                .nickname(sessionUser.getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
