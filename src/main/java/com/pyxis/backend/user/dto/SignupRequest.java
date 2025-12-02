package com.pyxis.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignupRequest {

    @NotBlank
    private String loginId;
    @NotBlank
    @Pattern(
            regexp = "^(?=\\S+$).{4,20}$",
            message = "비밀번호는 공백 없이 4~20자여야 합니다."
    )
    private String password;
    @NotBlank
    private String checkPassword;
    @NotBlank
    @Size(min = 2, max = 20, message = "닉네임은 2~10자여야 합니다.")
    private String nickname;
    @NotBlank
    private String gender;
    @NotBlank
    private String birthday;
    @NotBlank
    private String addressMain;

}
