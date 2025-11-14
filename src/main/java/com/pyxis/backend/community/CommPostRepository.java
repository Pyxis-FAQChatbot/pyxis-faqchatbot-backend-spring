package com.pyxis.backend.community;

import com.pyxis.backend.community.entity.CommPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommPostRepository extends JpaRepository<CommPost, Long> {
}
