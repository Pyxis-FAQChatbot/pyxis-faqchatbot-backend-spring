package com.pyxis.backend.community.dto;

import com.pyxis.backend.community.entity.CommPost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class CreateCommPostResponse {

    private Long commPostyId;
    private LocalDateTime createdAt;


    public static CreateCommPostResponse from(CommPost commPost) {
        return CreateCommPostResponse.builder()
                .commPostyId(commPost.getId())
                .createdAt(commPost.getCreatedAt())
                .build();
    }
}
