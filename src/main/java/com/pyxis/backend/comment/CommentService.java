package com.pyxis.backend.comment;

import com.pyxis.backend.ai.AiService;
import com.pyxis.backend.comment.dto.*;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommPostRepository commPostRepository;
    private final UserRepository userRepository;

    private final CommentQueryRepository commentQueryRepository;

    private final AiService aiService;

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

        if (parent != null) {
            commentRepository.increaseChildCount(parent.getId());
        }

        Comment saved = commentRepository.save(comment);

        registerAiFilterAfterCommit(sessionUser, request.getContent(), saved.getId());

        return CreateCommentResponse.of(saved, sessionUser);
    }

    @Transactional
    public void updateComment(Long communityId, Long commentId, UpdateCommentRequest request, SessionUser sessionUser) {

        Comment comment = validateCommentAccess(communityId, commentId, sessionUser);

        comment.updateContent(request.getContent());

        registerAiFilterAfterCommit(sessionUser, request.getContent(), comment.getId());
    }

    @Transactional
    public void deleteComment(Long communityId, Long commentId, SessionUser user) {

        Comment comment = validateCommentAccess(communityId, commentId, user);

        comment.delete();
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentListResponse> getCommentList(Long postId, Long parentId, int page, int size) {

        List<CommentListResponse> list;

        long total;
        if (parentId == null) {
            list = commentQueryRepository.getTopComments(postId, page, size);
            total = commentQueryRepository.countTopComments(postId);
        } else {
            list = commentQueryRepository.getChildComments(parentId, page, size);
            total = commentQueryRepository.countChildComments(parentId);
        }

        return PageResponse.of(list, page, size, total);
    }

    @Transactional(readOnly = true)
    public PageResponse<MyPageCommentListResponse> getCommentsByUser(SessionUser user, int page, int size) {
        List<MyPageCommentListResponse> list = commentQueryRepository.getCommentsByUserId(user.getId(), page, size);

        return PageResponse.of(list, page, size, commentQueryRepository.countCommentsByUserId(user.getId()));
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
        Long ownerId = commentQueryRepository.findCommentOwnerId(comment.getId());

        if (ownerId == null) {
            throw new CustomException(ErrorType.COMMENT_NOT_FOUND);
        }

        if (!Objects.equals(ownerId, user.getId())) {
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

    /**
     * 트랜잭션 커밋 이후(AfteCommit)에 AI 욕설/비방 필터링을 비동기로 실행하고,
     * 필터링 결과에 따라 댓글 상태(CommentStatus)를 업데이트하는 후처리 작업을 등록합니다.
     *
     * @param sessionUser  요청을 보낸 사용자 정보 (AI 필터링 시 사용자 컨텍스트 전달)
     * @param content      필터링할 댓글 내용
     * @param commentId    상태 업데이트 대상 댓글 ID
     */
    private void registerAiFilterAfterCommit(SessionUser sessionUser, String content, Long commentId) {
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    aiService.filterTextAsync(sessionUser, content)
                        .thenAccept(res -> aiService.updateCommentStatus(commentId, res))
                        .exceptionally(ex -> {
                            log.error("🚨 AI 필터링 중 에러 발생: {}", ex.getMessage(), ex);
                            return null;
                        });
                }
            }
        );
    }
}
