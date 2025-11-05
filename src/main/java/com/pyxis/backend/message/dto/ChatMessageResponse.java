package com.pyxis.backend.message.dto;

import com.pyxis.backend.message.entity.BotMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ChatMessageResponse {

    private Long botMessageId;
    private String botResponse;
    private List<?> sourceData;
    private LocalDateTime createdAt;


    public static ChatMessageResponse of(BotMessage botMessage) {

        return ChatMessageResponse.builder()
                .botMessageId(botMessage.getId())
                .botResponse(botMessage.getBotResponse())
                .sourceData(botMessage.getSourceData())
                .createdAt(botMessage.getCreatedAt())
                .build();
    }
}
