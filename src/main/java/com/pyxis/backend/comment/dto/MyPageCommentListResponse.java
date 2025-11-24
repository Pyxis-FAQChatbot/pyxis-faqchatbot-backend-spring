package com.pyxis.backend.comment.dto;

import com.pyxis.backend.comment.entity.CommentStatus;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record MyPageCommentListResponse(
        Long postId,
        Long commentId,
        Long parentId,
        String nickname,
        String content,
        LocalDateTime createdAt,
        int childCommentCount,
        CommentStatus status
) {
    @QueryProjection
    public MyPageCommentListResponse(Long postId,
                                     Long commentId,
                                     Long parentId,
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
        this.postId = postId;
        this.commentId = commentId;
        this.parentId = parentId;
        this.nickname = nickname;
        this.status = status;
        this.childCommentCount = childCommentCount;
        this.createdAt = createdAt;

    }
}
