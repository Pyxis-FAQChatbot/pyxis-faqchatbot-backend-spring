package com.pyxis.backend.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateCommentRequest {

    private Long parentId;
    @NotBlank
    private String content;
}
