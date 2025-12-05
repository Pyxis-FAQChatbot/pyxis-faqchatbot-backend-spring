package com.pyxis.backend.ai;

import com.pyxis.backend.ai.dto.AbuseFilterRequest;
import com.pyxis.backend.ai.dto.AbuseFilterResponse;
import com.pyxis.backend.ai.dto.AiChatRequest;
import com.pyxis.backend.comment.CommentRepository;
import com.pyxis.backend.comment.entity.CommentStatus;
import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.message.dto.BotResponse;
import com.pyxis.backend.user.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final WebClient fastApi8000Client;
    private final WebClient fastApi9000Client;

    private final CommentRepository commentRepository;

    public BotResponse chat(AiChatRequest request) {
        try {
            log.info("ClientQuery : {}",request.getQuery());

            BotResponse response = fastApi8000Client.post()
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

    @Async("asyncExecutor")
    public CompletableFuture<AbuseFilterResponse> filterTextAsync(SessionUser sessionUser,
                                                                  String query) {

        AbuseFilterRequest request = AbuseFilterRequest.of(sessionUser, query);

        return fastApi9000Client.post()
                .uri("/api/v1/filter/text")
                .bodyValue(request)
                .retrieve()
                // 4xx / 5xx일 때 처리 (원하면 커스텀 예외로 감싸기)
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.createException().flatMap(Mono::error)
                )
                .bodyToMono(AbuseFilterResponse.class)
                .toFuture(); // Mono -> CompletableFuture 로 변환
    }

    @Transactional
    public void updateCommentStatus(Long commentId, AbuseFilterResponse res) {
        if (res.isBlocked()) {
            int updated = commentRepository.updateStatus(commentId, CommentStatus.BLOCKED);
            if (updated == 0) {
                throw new CustomException(ErrorType.COMMENT_NOT_FOUND);
            }
        }
    }

}