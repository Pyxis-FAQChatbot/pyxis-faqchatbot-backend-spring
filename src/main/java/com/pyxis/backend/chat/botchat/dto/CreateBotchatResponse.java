package com.pyxis.backend.chat.botchat.dto;


import com.pyxis.backend.chat.botchat.entity.Botchat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class CreateBotchatResponse {
    private Long botChatId;
    private String title;
    private LocalDateTime createdAt;


    public static CreateBotchatResponse of(Botchat botchat){
        return CreateBotchatResponse.builder()
                .botChatId(botchat.getId())
                .title(botchat.getTitle())
                .createdAt(botchat.getCreatedAt())
                .build();
    }
}
