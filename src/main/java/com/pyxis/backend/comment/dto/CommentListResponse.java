package com.pyxis.backend.comment.dto;

import com.pyxis.backend.comment.entity.CommentStatus;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
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

    public static CommentListResponse from(Object[] r, int childCount) {

        Long id = ((Number) r[0]).longValue();
        String content = (String) r[1];
        Long userId = ((Number) r[2]).longValue();
        String nickname = (String) r[3];
        String status = (String) r[4];
        LocalDateTime createdAt = ((Timestamp) r[5]).toLocalDateTime();

        CommentStatus enumStatus = CommentStatus.valueOf(status);

        String displayContent;
        if (enumStatus == CommentStatus.DELETED) {
            displayContent = "삭제된 댓글입니다.";
        } else if (enumStatus == CommentStatus.BLOCKED) {
            displayContent = "욕설 또는 부적절한 표현으로 인해 가려진 댓글입니다.";
        } else {
            displayContent = content;
        }

        return CommentListResponse.builder()
                .commentId(id)
                .content(displayContent)
                .userId(userId)
                .nickname(nickname)
                .status(enumStatus)
                .childCommentCount(childCount)
                .createdAt(createdAt)
                .build();
    }
}

