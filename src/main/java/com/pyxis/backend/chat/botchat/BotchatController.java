package com.pyxis.backend.chat.botchat;

import com.pyxis.backend.chat.botchat.dto.BotchatListResponse;
import com.pyxis.backend.chat.botchat.dto.CreateBotchatRequest;
import com.pyxis.backend.chat.botchat.dto.CreateBotchatResponse;
import com.pyxis.backend.common.dto.PageResponse;
import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.user.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/chatbot/rooms")
    public ResponseEntity<PageResponse<BotchatListResponse>> getBotchatList(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable,
            HttpSession session) {

        SessionUser user = (SessionUser) session.getAttribute("user");
        if (user == null) {
            throw new CustomException(ErrorType.UNAUTHORIZED);
        }

        PageResponse<BotchatListResponse> response = botchatService.getBotchatList(user, pageable);

        return ResponseEntity.ok(response);
    }
}
