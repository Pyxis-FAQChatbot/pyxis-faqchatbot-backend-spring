package com.pyxis.backend.message;

import com.pyxis.backend.chat.botchat.BotchatRepository;
import com.pyxis.backend.chat.botchat.entity.Botchat;
import com.pyxis.backend.common.dto.PageResponse;
import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.message.dto.BotMessageResponse;
import com.pyxis.backend.message.dto.ChatMessageRequest;
import com.pyxis.backend.message.dto.ChatMessageResponse;
import com.pyxis.backend.message.dto.SourceData;
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


@Service
@RequiredArgsConstructor
public class BotMessageService {

    private final BotMessageRepository botMessageRepository;
    private final BotchatRepository botchatRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessageResponse createChatbotMessage(Long chatbotId, @Valid ChatMessageRequest request, SessionUser user) {
        Botchat botchat = botchatRepository.findById(chatbotId).orElseThrow(
                () -> new CustomException(ErrorType.BOTCHAT_NOT_FOUND)
        );
        Users users = userRepository.findById(user.getId()).orElseThrow(
                () -> new CustomException(ErrorType.USER_NOT_FOUND)
        );

        // restTemplate or WebClient 사용해서 통신 < 이후 개발 예정 >


        // 더미데이터

        SourceData sourceData = SourceData.builder()
                .title("ㅎㅇ")
                .source("sorce 입니다.")
                .snippet("snippet 입니다.")
                .url("http://더미데이터")
                .build();
        List<SourceData> ListSource = List.of(sourceData);

        BotMessage message = botMessageRepository.save(BotMessage.builder()
                .botchat(botchat)
                .user(users)
                .userQuery(request.getUserQuery())
                .botResponse("1")
                .sourceData(ListSource)
                .build());

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
}
