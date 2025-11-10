package com.pyxis.backend.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequest {

    private UserInfo user;
    private String query;
    private List<SessionHistory> sessionHistory;

    // ✅ 내부 클래스: UserInfo
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String loginId;
        private String nickname;
    }

    // ✅ 내부 클래스: SessionHistory
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionHistory {
        private String role;      // "user" 또는 "assistant"
        private String content;   // 대화 내용
    }
}