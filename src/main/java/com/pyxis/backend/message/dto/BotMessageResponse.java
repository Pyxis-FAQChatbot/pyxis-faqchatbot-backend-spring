package com.pyxis.backend.message.dto;

import com.pyxis.backend.message.entity.BotMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class BotMessageResponse {
    private Long id;
    private String userQuery;
    private String botResponse;
    private List<SourceData> sourceData;
    private List<String> followUpQuestions;
    private LocalDateTime createdAt;

    public static BotMessageResponse of(BotMessage message, List<String> followUpQuestions) {
        return BotMessageResponse.builder()
                .id(message.getId())
                .userQuery(message.getUserQuery())
                .botResponse(message.getBotResponse())
                .sourceData(message.getSourceData())
                .followUpQuestions(followUpQuestions)
                .createdAt(message.getCreatedAt())
                .build();
    }
}