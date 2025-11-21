package com.pyxis.backend.message.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BotResponse {

    private String queryTitle;
    private List<SourceDataDto> sourceData;
    private String botResponse;
    private List<String> followUpQuestions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SourceDataDto {
        private String title;
        private String source;
        private String url;
        private String snippet;
    }
}