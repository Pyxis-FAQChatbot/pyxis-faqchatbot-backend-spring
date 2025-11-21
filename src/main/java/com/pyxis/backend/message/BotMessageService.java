package com.pyxis.backend.message;

import com.pyxis.backend.ai.AiService;
import com.pyxis.backend.ai.dto.AiChatRequest;
import com.pyxis.backend.chat.botchat.BotchatRepository;
import com.pyxis.backend.chat.botchat.entity.Botchat;
import com.pyxis.backend.common.dto.PageResponse;
import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.message.dto.*;
import com.pyxis.backend.message.entity.BotMessage;
import com.pyxis.backend.user.UserRepository;
import com.pyxis.backend.user.dto.SessionUser;
import com.pyxis.backend.user.entity.Users;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class BotMessageService {

    private final BotMessageRepository botMessageRepository;
    private final BotchatRepository botchatRepository;
    private final UserRepository userRepository;

    private final AiService aiService;

    @Transactional
    public ChatMessageResponse createChatbotMessage(Long chatbotId, @Valid ChatMessageRequest request, SessionUser user) {
        Botchat botchat = botchatRepository.findById(chatbotId).orElseThrow(
                () -> new CustomException(ErrorType.BOTCHAT_NOT_FOUND)
        );
        Users users = userRepository.findById(user.getId()).orElseThrow(
                () -> new CustomException(ErrorType.USER_NOT_FOUND)
        );

        // ai service
        AiChatRequest.UserInfo userInfo = AiChatRequest.UserInfo.builder()
                .loginId(users.getLoginId())
                .nickname(users.getNickname())
                .build();

        AiChatRequest aiChatRequest = AiChatRequest.builder()
                .user(userInfo)
                .query(request.getUserQuery())
                .build();

        BotResponse aiResponse = aiService.chat(aiChatRequest);

        BotMessage message = botMessageRepository.save(BotMessage.builder()
                .botchat(botchat)
                .user(users)
                .userQuery(request.getUserQuery())
                .botResponse(aiResponse.getBotResponse())
                .sourceData(convertToSourceDataList(aiResponse.getSourceData()))
                .build());

        // 썸네일 제목
        if (botchat.getTitle() == null || botchat.getTitle().trim().isEmpty()) {
            botchat.updateTitle(aiResponse.getQueryTitle());
        }

        return ChatMessageResponse.of(message);

    }

    @Transactional(readOnly = true)
    public PageResponse<BotMessageResponse> chatbotMessageList(
            Long chatbotId,
            Pageable pageable,
            SessionUser sessionUser) {

        // 1. Botchat 존재 및 권한 확인
        Botchat botchat = botchatRepository.findById(chatbotId)
                .orElseThrow(() -> new CustomException(ErrorType.BOTCHAT_NOT_FOUND));

        if (!botchat.getUser().getId().equals(sessionUser.getId())) {
            throw new CustomException(ErrorType.USER_FORBIDDEN);
        }

        // 2. 메시지 조회 및 변환
        Page<BotMessage> botMessagePage = botMessageRepository
                .findByBotchatIdAndUserId(chatbotId, sessionUser.getId(), pageable);

        return PageResponse.of(
                botMessagePage.map(message -> BotMessageResponse.of(message, List.of()))
        );
    }

    private List<SourceData> convertToSourceDataList(List<BotResponse.SourceDataDto> dtos) {
        if (dtos == null) {
            return List.of();
        }

        return dtos.stream()
                .map(dto -> SourceData.builder()
                        .title(dto.getTitle())
                        .source(dto.getSource())
                        .url(dto.getUrl())
                        .snippet(dto.getSnippet())
                        .build())
                .toList();
    }
}
