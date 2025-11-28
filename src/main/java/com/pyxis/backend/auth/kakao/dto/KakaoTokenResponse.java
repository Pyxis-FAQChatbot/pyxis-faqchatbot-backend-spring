package com.pyxis.backend.auth.kakao.dto;

import lombok.Getter;

@Getter
public class KakaoTokenResponse {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String scope;
}
