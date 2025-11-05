package com.pyxis.backend.chat.botchat.dto;

import com.pyxis.backend.chat.botchat.entity.Botchat;
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
