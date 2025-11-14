package com.pyxis.backend.community;

import com.pyxis.backend.community.entity.CommPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommPostRepository extends JpaRepository<CommPost, Long> {
    @Modifying
    @Query("UPDATE CommPost c SET c.viewCount = c.viewCount + 1 WHERE c.id = :id")
    void incrementViewCount(Long id);
}
