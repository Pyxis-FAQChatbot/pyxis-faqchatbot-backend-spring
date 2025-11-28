package com.pyxis.backend.auth.naver.config;

import com.pyxis.backend.auth.naver.dto.NaverTokenResponse;
import com.pyxis.backend.auth.naver.dto.NaverUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class NaverApiClient {

    private final WebClient naverWebClient;

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    /**
     * 1) 인가코드(code) → AccessToken 요청
     */
    public NaverTokenResponse requestToken(String code, String state) {

        return naverWebClient.post()
                .uri("https://nid.naver.com/oauth2.0/token")
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("code", code)
                        .with("state", state)
                )
                .retrieve()
                .bodyToMono(NaverTokenResponse.class)
                .block();
    }

    /**
     * 2) AccessToken → 유저 정보 요청
     */
    public NaverUserInfoResponse requestUserInfo(String accessToken) {

        return naverWebClient.get()
                .uri("https://openapi.naver.com/v1/nid/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(NaverUserInfoResponse.class)
                .block();
    }
}
