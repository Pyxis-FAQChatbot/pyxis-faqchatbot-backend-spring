package com.pyxis.backend.comment;

import com.pyxis.backend.comment.dto.CommentListResponse;
import com.pyxis.backend.comment.dto.CreateCommentRequest;
import com.pyxis.backend.comment.dto.CreateCommentResponse;
import com.pyxis.backend.comment.dto.UpdateCommentRequest;
import com.pyxis.backend.comment.entity.Comment;
import com.pyxis.backend.comment.entity.CommentStatus;
import com.pyxis.backend.common.dto.PageResponse;
import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.community.CommPostRepository;
import com.pyxis.backend.community.entity.CommPost;
import com.pyxis.backend.user.UserRepository;
import com.pyxis.backend.user.dto.SessionUser;
import com.pyxis.backend.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommPostRepository commPostRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateCommentResponse createComment(Long communityId, CreateCommentRequest request, SessionUser sessionUser) {

        CommPost post = validatePostExists(communityId);

        // Users 조회 select 피하기 위해 프록시 조회
        Users user = userRepository.getReferenceById(sessionUser.getId());

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = validateParentComment(request.getParentId(), communityId);
        }

        Comment comment = Comment.builder()
                .commPost(post)
                .user(user)
                .parentComment(parent)
                .content(request.getContent())
                .status(CommentStatus.ACTIVE)
                .build();

        Comment saved = commentRepository.save(comment);

        return CreateCommentResponse.of(saved, sessionUser);
    }

    @Transactional
    public void updateComment(Long communityId, Long commentId, UpdateCommentRequest request, SessionUser user) {

        Comment comment = validateCommentAccess(communityId, commentId, user);

        comment.updateContent(request.getContent());
    }


    @Transactional
    public void deleteComment(Long communityId, Long commentId, SessionUser user) {

        Comment comment = validateCommentAccess(communityId, commentId, user);

        comment.delete();
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentListResponse> getCommentList(
            Long postId,
            Long parentId,
            int page,
            int size
    ) {
        int offset = page * size;

        List<Object[]> rows;
        long total;

        if (parentId == null) {
            rows = commentRepository.findTopComments(postId, size, offset);
            total = commentRepository.countTopComments(postId);
        } else {
            rows = commentRepository.findChildComments(parentId, size, offset);
            total = commentRepository.countChildCommentsByParent(parentId);
        }

        // 부모 댓글 / 대댓글 ID 목록 수집
        List<Long> parentIds = rows.stream()
                .map(r -> ((Number) r[0]).longValue())
                .toList();

        // 부모 댓글 ID가 없으면 → childCountMap도 비어있음
        final Map<Long, Long> childCountMap =
                parentIds.isEmpty()
                        ? Collections.emptyMap()
                        : commentRepository.countChildComments(parentIds)
                        .stream()
                        .collect(Collectors.toMap(
                                r -> ((Number) r[0]).longValue(),
                                r -> ((Number) r[1]).longValue()
                        ));

        List<CommentListResponse> list = rows.stream()
                .map(r -> {
                    Long id = ((Number) r[0]).longValue();
                    int childCount = childCountMap.getOrDefault(id, 0L).intValue();
                    return CommentListResponse.from(r, childCount);
                })
                .toList();

        return PageResponse.of(list, page, size, total);
    }

    /**
     * 게시글 존재 여부 검증
     */
    private CommPost validatePostExists(Long communityId) {
        return commPostRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(ErrorType.COMM_POST_NOT_FOUND));
    }

    /**
     * 댓글 존재 여부 검증
     */
    private Comment validateCommentExists(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorType.COMMENT_NOT_FOUND));
    }

    /**
     * 부모 댓글 검증
     */
    private Comment validateParentComment(Long parentId, Long communityId) {
        Comment parent = validateCommentExists(parentId);

        if (!parent.getCommPost().getId().equals(communityId)) {
            throw new CustomException(ErrorType.INVALID_PARENT_COMMENT);
        }

        return parent;
    }

    /**
     * 댓글이 해당 게시글에 속하는지 검증
     */
    private void validateCommentBelongsToPost(Comment comment, CommPost post) {
        if (!comment.getCommPost().getId().equals(post.getId())) {
            throw new CustomException(ErrorType.COMMENT_NOT_IN_POST);
        }
    }

    /**
     * 댓글 수정/삭제 가능한 상태인지 검증
     */
    private void validateCommentModifiable(Comment comment) {
        if (comment.getStatus() == CommentStatus.DELETED ||
                comment.getStatus() == CommentStatus.BLOCKED) {

            throw new CustomException(ErrorType.COMMENT_CANNOT_MODIFY);
        }
    }

    /**
     * 댓글 작성자 본인인지 검증
     */
    private void validateCommentOwner(Comment comment, SessionUser user) {
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorType.USER_FORBIDDEN);
        }
    }

    /**
     * 최종 통합 검증 (게시글 - 댓글 매칭 / 상태 / 사용자 권한)
     */
    private Comment validateCommentAccess(Long communityId, Long commentId, SessionUser user) {

        CommPost post = validatePostExists(communityId);
        Comment comment = validateCommentExists(commentId);

        validateCommentBelongsToPost(comment, post);
        validateCommentModifiable(comment);
        validateCommentOwner(comment, user);

        return comment;
    }
}
