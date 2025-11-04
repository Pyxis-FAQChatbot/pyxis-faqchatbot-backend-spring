package com.pyxis.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequest {

    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
    @NotBlank
    private String checkPassword;
    @NotBlank
    private String nickname;
    @NotBlank
    private String gender;
    @NotBlank
    private String birth;
    @NotBlank
    private String addressMain;

}
