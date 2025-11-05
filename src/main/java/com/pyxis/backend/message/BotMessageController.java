package com.pyxis.backend.message;

import com.pyxis.backend.common.dto.PageResponse;
import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.message.dto.BotMessageResponse;
import com.pyxis.backend.message.dto.ChatMessageRequest;
import com.pyxis.backend.message.dto.ChatMessageResponse;
import com.pyxis.backend.user.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BotMessageController {

    private final BotMessageService botMessageService;


    @PostMapping("/chatbot/{chatbotId}/message")
    public ResponseEntity<?> createChatbotMessage(@PathVariable Long chatbotId,
                                                  @Valid @RequestBody ChatMessageRequest request,
                                                  HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute("user");

        if (user == null) {
            throw new CustomException(ErrorType.UNAUTHORIZED);
        }

        ChatMessageResponse response = botMessageService.createChatbotMessage(chatbotId, request, user);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/chatbot/{chatbotId}/message")
    public ResponseEntity<?> chatbotMessageList(@PathVariable Long chatbotId,
                                                @PageableDefault(
                                                        size = 20,
                                                        sort = "createdAt",
                                                        direction = Sort.Direction.DESC
                                                ) Pageable pageable,
                                                HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute("user");

        if (user == null) {
            throw new CustomException(ErrorType.UNAUTHORIZED);
        }
        PageResponse<BotMessageResponse> response = botMessageService.chatbotMessageList(chatbotId, pageable, user);

        return ResponseEntity.ok(response);
    }
}
