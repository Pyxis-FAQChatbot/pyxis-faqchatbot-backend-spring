package com.pyxis.backend.comment;

import com.pyxis.backend.comment.entity.Comment;
import com.pyxis.backend.comment.entity.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Modifying
    @Query("update Comment c set c.childCount = c.childCount + 1 where c.id = :id")
    void increaseChildCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Comment c SET c.status = :status WHERE c.id = :commentId")
    int updateStatus(@Param("commentId") Long commentId,
                     @Param("status") CommentStatus status);
}

