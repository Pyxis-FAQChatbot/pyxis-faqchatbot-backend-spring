package com.pyxis.backend.comment;

import com.pyxis.backend.comment.dto.CreateCommentRequest;
import com.pyxis.backend.comment.dto.CreateCommentResponse;
import com.pyxis.backend.comment.entity.Comment;
import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.community.CommPostRepository;
import com.pyxis.backend.community.entity.CommPost;
import com.pyxis.backend.user.UserRepository;
import com.pyxis.backend.user.dto.SessionUser;
import com.pyxis.backend.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommPostRepository commPostRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateCommentResponse createComment(Long communityId, CreateCommentRequest request, SessionUser sessionUser) {

        // ai API 사용해서 댓글 욕설 검증

        CommPost commPost = commPostRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(ErrorType.COMM_POST_NOT_FOUND));

        // Users 조회를 굳이 select 하지 않기 위해 getReferenceById 사용
        Users users = userRepository.getReferenceById(sessionUser.getId());

        // 부모 댓글 처리
        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentFindById(request.getParentId());

            // 부모 댓글이 같은 게시글인지 검증 (중요!)
            if (!parent.getCommPost().getId().equals(communityId)) {
                throw new CustomException(ErrorType.INVALID_PARENT_COMMENT);
            }
        }

        Comment saveComment = commentRepository.save(
                Comment.builder()
                        .commPost(commPost)
                        .user(users)
                        .parentComment(parent)
                        .content(request.getContent())
                        .build()
        );

        return CreateCommentResponse.of(saveComment, sessionUser);
    }


    private Comment commentFindById(Long commentId){
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(ErrorType.COMMENT_NOT_FOUND)
        );
    }
}
