package com.pyxis.backend.auth.dto;

import lombok.Getter;

@Getter
public class KakaoUserInfo {
    private Long id;
    private KakaoAccount kakao_account;

    @Getter
    public static class KakaoAccount {
        private Profile profile;

        @Getter
        public static class Profile {
            private String nickname;
        }
    }
}
