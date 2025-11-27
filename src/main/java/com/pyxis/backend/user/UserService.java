package com.pyxis.backend.user;

import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import com.pyxis.backend.user.dto.LoginRequest;
import com.pyxis.backend.user.dto.PasswordChangeRequest;
import com.pyxis.backend.user.dto.SessionUser;
import com.pyxis.backend.user.dto.SignupRequest;
import com.pyxis.backend.user.entity.GenderType;
import com.pyxis.backend.user.entity.UserRole;
import com.pyxis.backend.user.entity.Users;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Users users = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new CustomException(ErrorType.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), users.getPassword())) {
            throw new CustomException(ErrorType.PASSWORD_NOT_MATCH);
        }

        return users;
    }

    @Transactional
    public Users changeNickname(String nickname, SessionUser user) {
        Users users = userRepository.findById(user.getId()).orElseThrow(
                () -> new CustomException(ErrorType.USER_NOT_FOUND)
        );
        existUsersByNickname(nickname);
        users.updateNickname(nickname);
        return users;
    }

    @Transactional
    public Users changeAddress(String address, SessionUser user) {
        Users users = userRepository.findById(user.getId()).orElseThrow(
                () -> new CustomException(ErrorType.USER_NOT_FOUND)
        );
        users.updateAddress(address);
        return users;
    }

    @Transactional
    public void changePassword(@Valid PasswordChangeRequest request, SessionUser user) {

        Users users = userRepository.findById(user.getId()).orElseThrow(
                () -> new CustomException(ErrorType.USER_NOT_FOUND)
        );
        if (!passwordEncoder.matches(request.getOldPassword(), users.getPassword())) {
            throw new CustomException(ErrorType.PASSWORD_NOT_MATCH);
        }

        if (passwordEncoder.matches(request.getNewPassword(), users.getPassword())) {
            throw new CustomException(ErrorType.SAME_PASSWORD_NOT_ALLOWED);
        }

        users.updatePassword(passwordEncoder.encode(request.getNewPassword()));
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
