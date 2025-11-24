package com.pyxis.backend.user.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class PasswordChangeRequest {

    @NotBlank
    @Pattern(
            regexp = "^(?=\\S+$).{4,20}$",
            message = "비밀번호는 공백 없이 4~20자여야 합니다."
    )
    private String oldPassword;
    @NotBlank
    @Pattern(
            regexp = "^(?=\\S+$).{4,20}$",
            message = "비밀번호는 공백 없이 4~20자여야 합니다."
    )
    private String newPassword;
}
