package com.pyxis.backend.user;

import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.user.dto.LoginRequest;
import com.pyxis.backend.user.dto.SessionUser;
import com.pyxis.backend.user.dto.SignupRequest;
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
        System.out.println("Set-Cookie: " + cookie.toString());

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
}
