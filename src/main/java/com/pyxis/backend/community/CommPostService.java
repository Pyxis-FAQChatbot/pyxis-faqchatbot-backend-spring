package com.pyxis.backend.community;

import com.pyxis.backend.common.dto.PageResponse;
import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.community.dto.CommPostListResponse;
import com.pyxis.backend.community.dto.CommPostRequest;
import com.pyxis.backend.community.dto.CreateCommPostResponse;
import com.pyxis.backend.community.dto.GetCommPostResponse;
import com.pyxis.backend.community.entity.CommPost;
import com.pyxis.backend.community.entity.PostType;
import com.pyxis.backend.user.UserRepository;
import com.pyxis.backend.user.dto.SessionUser;
import com.pyxis.backend.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CommPostService {

    private final CommPostRepository commPostRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateCommPostResponse createCommPost(CommPostRequest request, SessionUser user) {

        // ai api 욕설 필터링 검증 (비동기 방식)

        Users users = userRepository.findById(user.getId()).orElseThrow(
                () -> new CustomException(ErrorType.USER_NOT_FOUND)
        );

        CommPost saveCommPost = commPostRepository.save(CommPost.builder()
                .user(users)
                .postType(PostType.fromString(request.postType()))
                .title(request.title())
                .content(request.content())
                .build());


        return CreateCommPostResponse.from(saveCommPost);
    }

    @Transactional
    public GetCommPostResponse getCommPost(Long communityId) {

        commPostRepository.incrementViewCount(communityId);

        CommPost commPost = commPostRepository.findById(communityId).orElseThrow(
                () -> new CustomException(ErrorType.COMM_POST_NOT_FOUND)
        );
        Users user = commPost.getUser();

        return GetCommPostResponse.from(user, commPost);
    }

    @Transactional(readOnly = true)
    public PageResponse<CommPostListResponse> getCommPostList(int page, int size, String type) {

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);


        Page<CommPost> pageCommPost;
        if (null == type || type.isEmpty()) {
            // 전체 목록 조회
            pageCommPost = commPostRepository.findAllWithUser(pageable);
        } else {
            // 타입별 목록 조회
            PostType postType = PostType.fromString(type);
            pageCommPost = commPostRepository.findAllByPostTypeWithUser(postType, pageable);
        }

        return PageResponse.of(pageCommPost.map(CommPostListResponse::of));
    }

    @Transactional
    public void deleteCommPost(Long communityId, SessionUser sessionUser) {

        CommPost commPost = commPostRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(ErrorType.COMM_POST_NOT_FOUND));

        if (!sessionUser.getId().equals(commPost.getUser().getId())) {
            throw new CustomException(ErrorType.USER_FORBIDDEN);
        }

        commPostRepository.delete(commPost);
    }

    @Transactional
    public void updateCommPost(Long communityId, CommPostRequest request, Long sessionUserId) {
        // ai api 욕설 필터링 검증 (비동기 방식)

        CommPost commPost = commPostRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(ErrorType.COMM_POST_NOT_FOUND));

        if (!sessionUserId.equals(commPost.getUser().getId())) {
            throw new CustomException(ErrorType.USER_FORBIDDEN);
        }

        commPost.update(request);
    }
}
