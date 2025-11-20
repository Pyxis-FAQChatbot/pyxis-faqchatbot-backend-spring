package com.pyxis.backend.store;

import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.store.dto.StoreCreateRequest;
import com.pyxis.backend.user.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/stores")
    public ResponseEntity<?> createStore(
            @RequestBody @Valid StoreCreateRequest request,
            HttpSession session){
        SessionUser user = (SessionUser) session.getAttribute("user");

        if (user == null) {
            throw new CustomException(ErrorType.UNAUTHORIZED);
        }

        storeService.createStore(user, request);

        return ResponseEntity.ok().build();
    }
}
