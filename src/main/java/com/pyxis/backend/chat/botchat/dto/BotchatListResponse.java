package com.pyxis.backend.chat.botchat.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BotchatListResponse {
    private Long botchatId;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private String lasMessage;

}
