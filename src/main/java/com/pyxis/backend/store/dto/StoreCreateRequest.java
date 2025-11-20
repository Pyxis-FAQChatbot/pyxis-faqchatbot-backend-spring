package com.pyxis.backend.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class StoreCreateRequest {

    @NotBlank
    private String storeName;
    @NotBlank
    private String industryCode;
    @NotBlank
    private String address;
}
