package com.pyxis.backend.comment;

import com.pyxis.backend.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByCommPostIdAndParentCommentIsNull(Long communityId, Pageable pageable);

    Page<Comment> findByParentCommentId(Long parentId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parentComment.id = :commentId")
    int countChildComments(@Param("commentId") Long commentId);

}
