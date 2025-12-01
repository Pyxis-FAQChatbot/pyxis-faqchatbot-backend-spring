package com.pyxis.backend.user.dto;

import com.pyxis.backend.user.entity.GenderType;
import com.pyxis.backend.user.entity.UserRole;
import com.pyxis.backend.user.entity.UserSocialType;
import com.pyxis.backend.user.entity.Users;
import lombok.Builder;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class SessionUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String loginId;
    private String nickname;
    private UserRole role;
    private GenderType gender;
    private LocalDate birthday;
    private String addressMain;
    private UserSocialType userSocial;
    private LocalDateTime createdAt;

    public static SessionUser from(Users user) {
        return SessionUser.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .role(user.getRole())
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .addressMain(user.getAddressMain())
                .userSocial(user.getSocialType())
                .createdAt(user.getCreatedAt())
                .build();
    }
}