package com.pyxis.backend.comment;

import com.pyxis.backend.comment.dto.CommentListResponse;
import com.pyxis.backend.comment.dto.QCommentListResponse;
import com.pyxis.backend.comment.entity.QComment;
import com.pyxis.backend.user.entity.QUsers;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 최상위 댓글 조회 (+ childCount 포함)
     */
    @Override
    public List<CommentListResponse> getTopComments(Long postId, int page, int size) {

        QComment c = QComment.comment;
        QUsers u = QUsers.users;

        return queryFactory
                .select(new QCommentListResponse(
                        c.id,
                        u.id,
                        u.nickname,
                        c.content,
                        c.createdAt,
                        c.childCount,
                        c.status
                ))
                .from(c)
                .join(c.user, u)
                .where(
                        c.commPost.id.eq(postId)
                                .and(c.parentComment.isNull())
                )
                .orderBy(c.createdAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }


    /**
     * 대댓글 조회 (+ childCount 포함)
     */
    @Override
    public List<CommentListResponse> getChildComments(Long parentId, int page, int size) {

        QComment c = QComment.comment;
        QUsers u = QUsers.users;

        return queryFactory
                .select(new QCommentListResponse(
                        c.id,
                        u.id,
                        u.nickname,
                        c.content,
                        c.createdAt,
                        c.childCount,
                        c.status
                ))
                .from(c)
                .join(c.user, u)
                .where(c.parentComment.id.eq(parentId))
                .orderBy(c.createdAt.asc())
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }


    /**
     * 최상위 댓글 count
     */
    @Override
    public long countTopComments(Long postId) {
        QComment c = QComment.comment;

        Long result = queryFactory
                .select(c.count())
                .from(c)
                .where(
                        c.commPost.id.eq(postId)
                                .and(c.parentComment.isNull())
                )
                .fetchOne();

        return result != null ? result : 0L;
    }

    /**
     * 대댓글 count
     */
    @Override
    public long countChildComments(Long parentId) {

        QComment c = QComment.comment;

        Long result = queryFactory
                .select(c.count())
                .from(c)
                .where(c.parentComment.id.eq(parentId))
                .fetchOne();

        return result != null ? result : 0L;
    }

}
