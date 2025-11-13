package com.pyxis.backend.chat.botchat;

import com.pyxis.backend.chat.botchat.dto.BotchatListResponse;
import com.pyxis.backend.chat.botchat.dto.CreateBotchatRequest;
import com.pyxis.backend.chat.botchat.dto.CreateBotchatResponse;
import com.pyxis.backend.chat.botchat.entity.Botchat;
import com.pyxis.backend.common.dto.PageResponse;
import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.user.UserRepository;
import com.pyxis.backend.user.dto.SessionUser;
import com.pyxis.backend.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BotchatService {

    private final BotchatRepository botchatRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateBotchatResponse createBotchat(CreateBotchatRequest request, SessionUser sessionUser) {
        // 1. User 조회
        Users user = userRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new CustomException(ErrorType.USER_NOT_FOUND));

        // 2. Botchat 생성 및 저장
        Botchat botchat = botchatRepository.save(Botchat.builder()
                .title(request.getTitle())
                .user(user)
                .build());

        // 3. Response 반환
        return CreateBotchatResponse.of(botchat);
    }

    @Transactional
    public void deleteBotchat(SessionUser sessionUser, Long chatbotId) {
        Users user = userRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new CustomException(ErrorType.USER_NOT_FOUND));

        Botchat botchat = botchatRepository.findByIdAndUser(chatbotId, user).orElseThrow(
                () -> new CustomException(ErrorType.BOTCHAT_NOT_FOUND)
        );

        botchatRepository.delete(botchat);
    }

    @Transactional(readOnly = true)
    public PageResponse<BotchatListResponse> getBotchatList(SessionUser sessionUser, Pageable pageable) {
        Page<BotchatListResponse> responsePage = botchatRepository.findByUserIdWithLastMessage(
                sessionUser.getId(),
                pageable
        );

        return PageResponse.of(responsePage);
    }


}
