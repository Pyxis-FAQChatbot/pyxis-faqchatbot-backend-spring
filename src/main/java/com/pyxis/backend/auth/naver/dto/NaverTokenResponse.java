package com.pyxis.backend.auth.naver.dto;


import lombok.Getter;

@Getter
public class NaverTokenResponse {
    private String access_token;
    private String refresh_token;
    private String token_type;
    private String expires_in;
    private String error;
    private String error_description;
}
