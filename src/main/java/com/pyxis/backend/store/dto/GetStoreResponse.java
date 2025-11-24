package com.pyxis.backend.store.dto;

import com.pyxis.backend.store.entity.Store;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetStoreResponse {

    private Long storeId;
    private String storeName;
    private String industryCode;
    private String address;
    private LocalDateTime createdAt;

    public static GetStoreResponse from(Store store) {
        return GetStoreResponse.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .industryCode(store.getIndustryCode())
                .address(store.getAddress())
                .createdAt(store.getCreatedAt())
                .build();
    }
}
