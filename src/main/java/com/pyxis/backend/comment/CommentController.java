package com.pyxis.backend.comment;

import com.pyxis.backend.comment.dto.CreateCommentRequest;
import com.pyxis.backend.comment.dto.CreateCommentResponse;
import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.user.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/community/{communityId}/comment")
    public ResponseEntity<?> createComment(
            @PathVariable Long communityId,
            @RequestBody @Valid CreateCommentRequest request,
            HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute("user");

        if (user == null) {
            throw new CustomException(ErrorType.UNAUTHORIZED);
        }

        CreateCommentResponse response = commentService.createComment(communityId, request, user);

        return ResponseEntity.ok(response);
    }
}
