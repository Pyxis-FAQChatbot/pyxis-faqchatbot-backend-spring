package com.pyxis.backend.comment.dto;

import com.pyxis.backend.comment.entity.CommentStatus;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record CommentListResponse(Long commentId, Long userId, String nickname, String content, LocalDateTime createdAt,
                                  int childCommentCount, CommentStatus status) {

    @QueryProjection
    public CommentListResponse(Long commentId,
                               Long userId,
                               String nickname,
                               String content,
                               LocalDateTime createdAt,
                               int childCommentCount,
                               CommentStatus status) {

        if (status == CommentStatus.DELETED) {
            this.content = "삭제된 댓글입니다.";
        } else if (status == CommentStatus.BLOCKED) {
            this.content = "욕설 또는 부적절한 표현으로 인해 가려진 댓글입니다.";
        } else {
            this.content = content;
        }

        this.commentId = commentId;
        this.userId = userId;
        this.nickname = nickname;
        this.status = status;
        this.childCommentCount = childCommentCount;
        this.createdAt = createdAt;


    }
}

