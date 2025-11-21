package com.pyxis.backend.comment;

import com.pyxis.backend.comment.dto.CommentListResponse;

import java.util.List;

public interface CommentQueryRepository {

    List<CommentListResponse> getTopComments(Long postId, int page, int size);

    List<CommentListResponse> getChildComments(Long parentId, int page, int size);

    long countTopComments(Long postId);

    long countChildComments(Long parentId);
}
