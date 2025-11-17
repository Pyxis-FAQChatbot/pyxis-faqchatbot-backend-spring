package com.pyxis.backend.user;

import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.user.dto.LoginRequest;
import com.pyxis.backend.user.dto.SignupRequest;
import com.pyxis.backend.user.entity.GenderType;
import com.pyxis.backend.user.entity.UserRole;
import com.pyxis.backend.user.entity.Users;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;  // ✅ 올바른 임포트import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
                .birthday(LocalDate.parse(request.getBirthday()))
                .addressMain(request.getAddressMain())

                .build());
    }

    @Transactional(readOnly = true)
    public Users login(@Valid LoginRequest request) {
        Users user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new CustomException(ErrorType.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorType.PASSWORD_NOT_MATCH);
        }

        return user;
    }


    public void existUsersByNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorType.NICKNAME_ALREADY_EXISTS);
        }
    }

    public void existUsersByLoginId(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new CustomException(ErrorType.LOGINID_ALREADY_EXISTS);
        }
    }


}
