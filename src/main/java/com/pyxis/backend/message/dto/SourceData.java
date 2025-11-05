package com.pyxis.backend.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SourceData {
    private String title;
    private String source;
    private String url;
    private String snippet;
}