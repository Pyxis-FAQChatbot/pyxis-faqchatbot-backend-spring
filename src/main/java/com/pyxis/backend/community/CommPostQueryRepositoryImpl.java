package com.pyxis.backend.community;

import com.pyxis.backend.comment.entity.QComment;
import com.pyxis.backend.community.dto.*;
import com.pyxis.backend.community.entity.PostType;
import com.pyxis.backend.community.entity.QCommPost;
import com.pyxis.backend.user.dto.SessionUser;
import com.pyxis.backend.user.entity.QUsers;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommPostQueryRepositoryImpl implements CommPostQueryRepository {

    private final JPAQueryFactory queryFactory;

    private static final QCommPost c = QCommPost.commPost;
    private static final QComment cm = QComment.comment;
    private static final QUsers u = QUsers.users;

    @Override
    public Page<CommPostListResponse> searchPosts(PostType type, String query, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        if (type != null) {
            builder.and(c.postType.eq(type));
        }

        if (query != null && !query.isEmpty()) {
            builder.and(c.title.containsIgnoreCase(query));
        }

        List<CommPostListResponse> content = queryFactory
                .select(new QCommPostListResponse(
                        c.id,
                        c.title,
                        c.content,
                        u.id,
                        u.nickname,
                        c.postType,
                        c.imageURL,
                        c.viewCount,
                        c.createdAt,
                        JPAExpressions.select(cm.count())
                                .from(cm)
                                .where(cm.commPost.id.eq(c.id))
                ))
                .from(c)
                .join(c.user, u)
                .where(builder)
                .orderBy(c.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(c.count())
                .from(c)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public List<MyPagePostListResponse> getPostsByUser(SessionUser user, int page, int size) {

        return queryFactory
                .select(new QMyPagePostListResponse(
                        c.id,
                        c.title,
                        c.content,
                        c.postType,
                        c.imageURL,
                        c.viewCount,
                        c.createdAt,
                        JPAExpressions.select(cm.count())
                                .from(cm)
                                .where(cm.commPost.id.eq(c.id))
                ))
                .from(c)
                .join(c.user, u)
                .where(c.user.id.eq(user.getId()))
                .orderBy(c.createdAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    @Override
    public long countCommentsByUserId(Long userId) {
        QCommPost c = QCommPost.commPost;

        Long count = queryFactory
                .select(c.count())
                .from(c)
                .where(c.user.id.eq(userId))
                .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public GetCommPostResponse getPostDetail(Long communityId) {

        return queryFactory
                .select(new QGetCommPostResponse(
                        u.id,
                        u.nickname,
                        c.title,
                        c.content,
                        c.postType,
                        c.imageURL,
                        c.viewCount,
                        c.createdAt,
                        JPAExpressions
                                .select(cm.count())
                                .from(cm)
                                .where(cm.commPost.id.eq(c.id))
                ))
                .from(c)
                .join(c.user, u)
                .where(c.id.eq(communityId))
                .fetchOne();
    }
}
