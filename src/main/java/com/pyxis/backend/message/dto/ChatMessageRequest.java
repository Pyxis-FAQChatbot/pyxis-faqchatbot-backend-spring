package com.pyxis.backend.message.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChatMessageRequest {

    @NotBlank
    private String userQuery;
}
