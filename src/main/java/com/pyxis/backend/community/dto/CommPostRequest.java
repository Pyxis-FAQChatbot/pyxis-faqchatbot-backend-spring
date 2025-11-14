package com.pyxis.backend.community.dto;

import jakarta.validation.constraints.NotBlank;

public record CommPostRequest(@NotBlank String title, @NotBlank String content, @NotBlank String postType) {

}
