package com.pyxis.backend.chat.botchat;

import com.pyxis.backend.chat.botchat.dto.CreateBotchatRequest;
import com.pyxis.backend.chat.botchat.dto.CreateBotchatResponse;
import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.user.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BotchatController {

    private final BotchatService botchatService;

    @PostMapping("/chatbot")
    public ResponseEntity<?> createBotchat(@RequestBody(required = false) CreateBotchatRequest request, HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute("user");
        if (user == null) {
            throw new CustomException(ErrorType.UNAUTHORIZED);
        }

        if (request == null) {
            request = new CreateBotchatRequest();
        }

        CreateBotchatResponse response = botchatService.createBotchat(request, user);
        return ResponseEntity.ok(response);
    }
}
