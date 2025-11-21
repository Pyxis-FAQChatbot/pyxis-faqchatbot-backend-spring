package com.pyxis.backend.community;

import com.pyxis.backend.community.entity.CommPost;
import com.pyxis.backend.community.entity.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommPostQueryRepository {

    Page<CommPost> searchPosts(PostType type, String query, Pageable pageable);
}
