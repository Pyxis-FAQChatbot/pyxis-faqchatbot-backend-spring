package com.pyxis.backend.auth.naver;

import com.pyxis.backend.user.dto.SessionUser;
import com.pyxis.backend.user.entity.Users;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NaverLoginController {

    private final NaverLoginService naverLoginService;

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.redirect-uri}")
    private String redirectUri;

    @Value("${naver.frontend.redirect-uri}")
    private String frontRedirectUri;

    /**
     * 1) 프론트에서 /login/naver 호출 → 네이버 로그인 페이지로 redirect
     */
    @GetMapping("/login/naver")
    public String redirectToNaver() {

        String state = naverLoginService.generateState(); // CSRF 방지용 랜덤값
        String naverUrl =
                "https://nid.naver.com/oauth2.0/authorize" +
                        "?response_type=code" +
                        "&client_id=" + clientId +
                        "&redirect_uri=" + redirectUri +
                        "&state=" + state;

        log.info("🚀 Redirecting to Naver Login URL: {}", naverUrl);

        return "redirect:" + naverUrl;
    }

    /**
     * 2) 네이버 로그인 성공 → 네이버 → redirect_uri?code=&state= 로 호출
     */
    @GetMapping("/login/naver/callback")
    public ResponseEntity<?> naverCallback(
            String code,
            String state,
            HttpSession session,
            HttpServletResponse response
    ) {

        if (code == null) {
            log.error("❌ 네이버 로그인 실패: code 없음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("🎉 Naver callback received! code={} state={}", code, state);

        // 로그인 처리 (기존 회원 or 신규 회원)
        Users user = naverLoginService.naverLogin(code, state);

        // 세션 생성
        SessionUser sessionUser = SessionUser.from(user);
        session.setAttribute("user", sessionUser);

        ResponseCookie cookie = ResponseCookie.from("JSESSIONID", session.getId())
                .path("/")
                .httpOnly(true)
                .secure(true) // 로컬에서는 false
                .sameSite("None")
                .maxAge(60 * 60 * 24)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        log.info(" NAVER Session ID: {}", session.getId());
        log.info(" NAVER Set-Cookie: {}", cookie);

        // 로그인 후 프론트로 이동
        return ResponseEntity.status(302)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.LOCATION, frontRedirectUri)
                .build();
    }
}
