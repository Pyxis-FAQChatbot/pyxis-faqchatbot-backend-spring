package com.pyxis.backend.community;

import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.community.dto.CommPostRequest;
import com.pyxis.backend.community.dto.CreateCommPostResponse;
import com.pyxis.backend.community.dto.GetCommPostResponse;
import com.pyxis.backend.user.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommPostController {

    private final CommPostService commPostService;

    @PostMapping(value = "/community", consumes = "multipart/form-data")
    public ResponseEntity<CreateCommPostResponse> createCommPost(
            @RequestPart("data") @Valid CommPostRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpSession session) {

        SessionUser user = (SessionUser) session.getAttribute("user");
        if (user == null) {
            throw new CustomException(ErrorType.UNAUTHORIZED);
        }

        CreateCommPostResponse response = commPostService.createCommPost(request, file, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/community/{communityId}")
    public ResponseEntity<?> getCommPost(@PathVariable Long communityId) {
        GetCommPostResponse response = commPostService.getCommPost(communityId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/community/posts")
    public ResponseEntity<?> getCommPostList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String query) {

        var response = commPostService.getCommPostList(page, size, type, query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/community/{communityId}")
    public ResponseEntity<?> deleteCommPost(@PathVariable Long communityId, HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute("user");

        if (user == null) {
            throw new CustomException(ErrorType.UNAUTHORIZED);
        }

        commPostService.deleteCommPost(communityId, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/community/{communityId}")
    public ResponseEntity<?> updateCommPost(@PathVariable Long communityId, @RequestBody
    CommPostRequest request, HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute("user");

        if (user == null) {
            throw new CustomException(ErrorType.UNAUTHORIZED);
        }

        commPostService.updateCommPost(communityId, request, user.getId());

        return ResponseEntity.noContent().build();
    }
}
