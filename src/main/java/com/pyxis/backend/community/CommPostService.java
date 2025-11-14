package com.pyxis.backend.community;

import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.community.dto.CreateCommPostRequest;
import com.pyxis.backend.community.dto.CreateCommPostResponse;
import com.pyxis.backend.community.dto.GetCommPostResponse;
import com.pyxis.backend.community.entity.CommPost;
import com.pyxis.backend.community.entity.PostType;
import com.pyxis.backend.user.UserRepository;
import com.pyxis.backend.user.dto.SessionUser;
import com.pyxis.backend.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommPostService {

    private final CommPostRepository commPostRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateCommPostResponse createCommPost(CreateCommPostRequest request, SessionUser user) {

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
}
