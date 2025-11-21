package com.pyxis.backend.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AbuseFilterResponse {

    private String originQuery;
    private double toxicity;
    private double insult;
    private double profanity;
    private double hate;
    private double threat;
    private boolean blocked;
}