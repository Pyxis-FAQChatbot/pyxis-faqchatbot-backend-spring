package com.pyxis.backend.store;

import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.store.dto.GetStoreResponse;
import com.pyxis.backend.store.dto.StoreCreateRequest;
import com.pyxis.backend.store.dto.StoreUpdateRequest;
import com.pyxis.backend.store.entity.Store;
import com.pyxis.backend.user.UserRepository;
import com.pyxis.backend.user.dto.SessionUser;
import com.pyxis.backend.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createStore(SessionUser user, StoreCreateRequest request) {
        Users users = userRepository.findById(user.getId()).orElseThrow(
                () -> new CustomException(ErrorType.USER_NOT_FOUND)
        );

        if (storeRepository.existsByUser(users)) {
            throw new CustomException(ErrorType.STORE_ALREADY_EXISTS);
        }

        storeRepository.save(Store.builder()
                                    .user(users)
                                    .name(request.getStoreName())
                                    .industryCode(request.getIndustryCode())
                                    .address(request.getAddress())
                                    .build());
    }

    @Transactional
    public void update(SessionUser user, StoreUpdateRequest request) {
        Users users = userRepository.findById(user.getId()).orElseThrow(
                () -> new CustomException(ErrorType.USER_NOT_FOUND)
        );

        Store store = storeRepository.findByUser(users)
                .orElseThrow(() -> new CustomException(ErrorType.STORE_NOT_FOUND));

        store.update(request);
    }

    @Transactional(readOnly = true)
    public GetStoreResponse getStore(SessionUser user) {
        Users users = userRepository.findById(user.getId()).orElseThrow(
                () -> new CustomException(ErrorType.USER_NOT_FOUND)
        );

        Store store = storeRepository.findByUser(users)
                .orElseThrow(() -> new CustomException(ErrorType.STORE_NOT_FOUND));

        return GetStoreResponse.from(store);
    }
}
