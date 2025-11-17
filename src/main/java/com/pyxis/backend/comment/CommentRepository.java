package com.pyxis.backend.comment;

import com.pyxis.backend.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 부모 댓글 리스트 조회
    @Query(value = """
            SELECT c.id,
                c.content,
                c.user_id,
                u.nickname,
                c.status,
                c.created_at
            FROM comment c
            JOIN users u ON u.id = c.user_id
            WHERE c.comm_post_id = :postId
              AND c.parent_id IS NULL
            ORDER BY c.created_at ASC
            LIMIT :limit OFFSET :offset
            """,
            nativeQuery = true)
    List<Object[]> findTopComments(
            @Param("postId") Long postId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    // 부모 댓글 count
    @Query(value = """
            SELECT COUNT(*)
            FROM comment
            WHERE comm_post_id = :postId
              AND parent_id IS NULL
            """,
            nativeQuery = true)
    long countTopComments(@Param("postId") Long postId);

    // 대댓글 리스트 조회
    @Query(value = """
            SELECT c.id,
                c.content,
                c.user_id,
                u.nickname,
                c.status,
                c.created_at
            FROM comment c
            JOIN users u ON u.id = c.user_id
            WHERE c.parent_id = :parentId
            ORDER BY c.created_at ASC
            LIMIT :limit OFFSET :offset
            """,
            nativeQuery = true)
    List<Object[]> findChildComments(
            @Param("parentId") Long parentId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    // 대댓글 count
    @Query(value = """
            SELECT COUNT(*)
            FROM comment
            WHERE parent_id = :parentId
            """,
            nativeQuery = true)
    long countChildCommentsByParent(@Param("parentId") Long parentId);

    // IN 절 + GROUP BY childCount
    @Query(value = """
            SELECT parent_id, COUNT(*)
            FROM comment
            WHERE parent_id IN (:parentIds)
            GROUP BY parent_id
            """,
            nativeQuery = true)
    List<Object[]> countChildComments(@Param("parentIds") List<Long> parentIds);
}

