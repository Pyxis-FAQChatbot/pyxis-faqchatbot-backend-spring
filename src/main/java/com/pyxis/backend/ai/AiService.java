package com.pyxis.backend.ai;

import com.pyxis.backend.ai.dto.AiChatRequest;
import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.message.dto.BotResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final WebClient webClient;

    public BotResponse chat(AiChatRequest request) {
        try {
            log.info("AI API 호출 - query: {}, historySize: {}",
                    request.getQuery(),
                    request.getSessionHistory().size());

            BotResponse response = webClient.post()
                    .uri("/api/v1/query")  // ✅ AI API 엔드포인트
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(BotResponse.class)
                    .block();

            log.info("AI API 응답 성공");
            return response;

        } catch (WebClientResponseException e) {
            log.error("AI API 오류 - 상태코드: {}, 응답: {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString());
            throw new CustomException(ErrorType.AI_API_ERROR);

        } catch (Exception e) {
            log.error("AI API 호출 실패: {}", e.getMessage(), e);
            throw new CustomException(ErrorType.AI_API_ERROR);
        }
    }
}