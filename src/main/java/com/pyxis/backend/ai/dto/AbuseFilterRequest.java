package com.pyxis.backend.ai.dto;

import com.pyxis.backend.user.dto.SessionUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AbuseFilterRequest {
    private AbuseFilterUserDto user;
    private String query;

    public static AbuseFilterRequest of(SessionUser sessionUser, String query) {
        return AbuseFilterRequest.builder()
                .user(
                        AbuseFilterUserDto.builder()
                                .loginId(sessionUser.getLoginId())
                                .nickname(sessionUser.getNickname())
                                .build()
                )
                .query(query)
                .build();
    }
}
