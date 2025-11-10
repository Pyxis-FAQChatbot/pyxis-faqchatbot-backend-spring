package com.pyxis.backend.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BotResponse {

    private List<SourceDataDto> sourceData;
    private String botResponse;
    private List<String> followUpQuestions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SourceDataDto {
        private String title;
        private String source;
        private String url;
        private String snippet;
    }
}