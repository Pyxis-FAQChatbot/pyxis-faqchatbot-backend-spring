package com.pyxis.backend.user;

import com.pyxis.backend.comment.CommentService;
import com.pyxis.backend.comment.dto.MyPageCommentListResponse;
import com.pyxis.backend.common.dto.PageResponse;
import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.community.CommPostService;
import com.pyxis.backend.user.dto.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final CommentService commentService;
    private final CommPostService commPostService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        userService.signup(request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        userService.existUsersByNickname(nickname);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-id")
    public ResponseEntity<?> checkLoginId(@RequestParam String loginId) {
        userService.existUsersByLoginId(loginId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpSession session,
                                   HttpServletResponse response) {
        SessionUser sessionUser = SessionUser.from(userService.login(request));
        session.setAttribute("user", sessionUser);

        ResponseCookie cookie = ResponseCookie.from("JSESSIONID", session.getId())
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(60 * 60 * 24)  // 24시간
                .build();

        // 헤더에 추가
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 디버깅용 로그
        System.out.println("Session ID: " + session.getId());
        System.out.println("Set-Cookie: " + cookie);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        log.info("로그아웃 성공");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<SessionUser> getCurrentUser(HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute("user");

        if (user == null) {
            throw new CustomException(ErrorType.UNAUTHORIZED);
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/mypage/comments")
    public ResponseEntity<PageResponse<MyPageCommentListResponse>> getMyComments(
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        SessionUser user = (SessionUser) session.getAttribute("user");
        if (user == null) {
            throw new CustomException(ErrorType.UNAUTHORIZED);
        }

        return ResponseEntity.ok(commentService.getCommentsByUser(user, page, size));
    }

    @GetMapping("/mypage/posts")
    public ResponseEntity<?> getMyPosts(
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        SessionUser user = (SessionUser) session.getAttribute("user");
        if (user == null) {
            throw new CustomException(ErrorType.UNAUTHORIZED);
        }
        return ResponseEntity.ok(commPostService.getPostsByUser(user, page, size));
    }

    @PatchMapping("/mypage/password")
    public ResponseEntity<?> changePassword(
            @RequestBody @Valid PasswordChangeRequest request,
            HttpSession session) {

        SessionUser user = (SessionUser) session.getAttribute("user");
        if (user == null) {
            throw new CustomException(ErrorType.UNAUTHORIZED);
        }

        userService.changePassword(request, user);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/mypage/nickname")
    public ResponseEntity<?> changeNickname(@RequestBody @Valid ChangeNicknameRequest request,
                                            HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute("user");
        if (user == null) {
            throw new CustomException(ErrorType.UNAUTHORIZED);
        }
        SessionUser sessionUser = SessionUser.from(userService.changeNickname(request.getNewNickname(), user));

        session.setAttribute("user", sessionUser);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/mypage/address")
    public ResponseEntity<?> changeAddress(@RequestBody @Valid ChangeAddressRequest request,
                                            HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute("user");
        if (user == null) {
            throw new CustomException(ErrorType.UNAUTHORIZED);
        }
        SessionUser sessionUser = SessionUser.from(userService.changeAddress(request.getNewAddress(), user));

        session.setAttribute("user", sessionUser);

        return ResponseEntity.ok().build();
    }
}
