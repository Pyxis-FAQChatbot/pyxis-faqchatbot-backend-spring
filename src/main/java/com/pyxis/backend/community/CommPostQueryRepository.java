package com.pyxis.backend.community;

import com.pyxis.backend.community.dto.CommPostListResponse;
import com.pyxis.backend.community.dto.GetCommPostResponse;
import com.pyxis.backend.community.dto.MyPagePostListResponse;
import com.pyxis.backend.community.entity.PostType;
import com.pyxis.backend.user.dto.SessionUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommPostQueryRepository {

    Page<CommPostListResponse> searchPosts(PostType type, String query, Pageable pageable);

    List<MyPagePostListResponse> getPostsByUser(SessionUser user, int page, int size);

    long countCommentsByUserId(Long userId);

    GetCommPostResponse getPostDetail(Long communityId);
}
