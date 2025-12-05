package com.pyxis.backend.auth.kakao;

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
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.frontend.redirect-uri}")   // ← 새 환경변수
    private String frontRedirectUri;
    /**
     * 1) 프론트에서 /login/kakao 호출 → 백엔드에서 카카오 로그인 페이지로 redirect
     */
    @GetMapping("/login/kakao")
    public String redirectToKakao() {
        String kakaoUrl =
                "https://kauth.kakao.com/oauth/authorize" +
                        "?client_id=" + clientId +
                        "&redirect_uri=" + redirectUri +
                        "&response_type=code";

        log.info("🚀 Redirecting to Kakao Login URL: {}", kakaoUrl);

        return "redirect:" + kakaoUrl;
    }

    /**
     * 2) 카카오 로그인 성공 → Kakao → redirect_uri?code=xxx 로 호출
     *    백엔드가 code로 토큰/유저정보 요청 → 세션 생성
     */
    @GetMapping("/login/kakao/callback")
    public ResponseEntity<?> kakaoCallback(String code, HttpSession session, HttpServletResponse response) {

        if (code == null) {
            log.error("❌ 카카오 로그인 실패: code 없음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("🎉 Kakao callback received! code={}", code);

        // 로그인 처리 (기존 회원 or 신규 회원 가입)
        Users user = kakaoLoginService.kakaoLogin(code);

        // 세션 생성
        SessionUser sessionUser = SessionUser.from(user);
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

        System.out.println(" KAKAO Session ID: " + session.getId());
        System.out.println(" KAKAO Set-Cookie: " + cookie);

        // 로그인 후 프론트 메인 페이지로 이동
        return ResponseEntity.status(302)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.LOCATION, frontRedirectUri) // React 메인으로 보내기
                .build();
    }
}
