package com.pyxis.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChangeAddressRequest {

    @NotBlank
    private String newAddress;
}
