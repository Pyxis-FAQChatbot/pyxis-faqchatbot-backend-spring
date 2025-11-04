package com.pyxis.backend.user;

import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.user.dto.SignupRequest;
import com.pyxis.backend.user.entity.GenderType;
import com.pyxis.backend.user.entity.UserRole;
import com.pyxis.backend.user.entity.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(SignupRequest request) {

        existUsersByLoginId(request.getLoginId());
        existUsersByNickname(request.getNickname());

        if (!request.getPassword().equals(request.getCheckPassword())) {
            throw new CustomException(ErrorType.PASSWORD_NOT_MATCH);
        }

        userRepository.save(Users.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .gender(GenderType.fromString(request.getGender()))
                .role(UserRole.USER)
                .birth(LocalDate.parse(request.getBirth()))
                .addressMain(request.getAddressMain())

                .build());
    }


    public void existUsersByNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorType.NICKNAME_ALREADY_EXISTS, List.of("nickname"));
        }
    }

    public void existUsersByLoginId(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new CustomException(ErrorType.LOGINID_ALREADY_EXISTS, List.of("loginId"));
        }
    }
}
