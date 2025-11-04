package com.pyxis.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {
    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
}
