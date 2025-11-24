package com.pyxis.backend.store.dto;

import lombok.Getter;

@Getter
public class StoreUpdateRequest {
    private String storeName;
    private String industryCode;
    private String address;
}
