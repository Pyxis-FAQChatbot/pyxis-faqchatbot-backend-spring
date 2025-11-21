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
public class AbuseFilterUserDto {

    private String loginId;
    private String nickname;

}
