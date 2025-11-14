package com.pyxis.backend.community.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommPostRequest(@NotBlank String title, @NotBlank String content, @NotBlank String postType) {

}
