package com.pyxis.backend.comment.dto;

import com.pyxis.backend.comment.entity.Comment;
import com.pyxis.backend.comment.entity.CommentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentListResponse {

    private Long commentId;
    private String content;
    private Long userId;
    private String nickname;
    private CommentStatus status;
    private int childCommentCount;
    private LocalDateTime createdAt;

    public static CommentListResponse from(Comment comment, int childCount) {
        String content;

        if (comment.getStatus() == CommentStatus.BLOCKED) {
            // 관리자 또는 신고 누적으로 숨김 처리된 경우
            content = "욕설 또는 부적절한 표현으로 인해 가려진 댓글입니다.";
        } else if (comment.getStatus() == CommentStatus.DELETED) {
            // 사용자가 직접 삭제한 경우
            content = "삭제된 댓글입니다.";
        } else {
            // 정상 댓글
            content = comment.getContent();
        }

        return CommentListResponse.builder()
                .commentId(comment.getId())
                .content(content)
                .userId(comment.getUser().getId())
                .nickname(comment.getUser().getNickname())
                .status(comment.getStatus())
                .childCommentCount(childCount)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}

