package com.pyxis.backend.community;

import com.pyxis.backend.community.entity.CommPost;
import com.pyxis.backend.community.entity.PostType;
import com.pyxis.backend.community.entity.QCommPost;
import com.pyxis.backend.user.entity.QUsers;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommPostQueryRepositoryImpl implements CommPostQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CommPost> searchPosts(PostType type, String query, Pageable pageable) {
        QCommPost c = QCommPost.commPost;
        QUsers u = QUsers.users;

        BooleanBuilder builder = new BooleanBuilder();

        if (type != null) {
            builder.and(c.postType.eq(type));
        }

        if (query != null && !query.isEmpty()) {
            builder.and(c.title.containsIgnoreCase(query));
        }

        List<CommPost> content = queryFactory
                .selectFrom(c)
                .join(c.user, u).fetchJoin()
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
}
