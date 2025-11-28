package com.pyxis.backend.auth.naver.dto;


import lombok.Getter;

@Getter
public class NaverUserInfoResponse {

    private String resultcode;
    private String message;
    private Response response;

    @Getter
    public static class Response {
        private String id;
        private String nickname;
        private String gender;
        private String birthday;
        private String birthyear;
    }
}
