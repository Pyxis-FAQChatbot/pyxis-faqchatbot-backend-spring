package com.pyxis.backend.common.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PageResponse<T> {
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int size;
    private boolean first;
    private boolean last;
    private List<T> items;

    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .size(page.getSize())
                .first(page.isFirst())
                .last(page.isLast())
                .items(page.getContent())
                .build();
    }
}