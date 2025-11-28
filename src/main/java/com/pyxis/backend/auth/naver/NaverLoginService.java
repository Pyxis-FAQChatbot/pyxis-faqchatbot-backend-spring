package com.pyxis.backend.auth.naver;

import com.pyxis.backend.auth.naver.config.NaverApiClient;
import com.pyxis.backend.auth.naver.dto.NaverTokenResponse;
import com.pyxis.backend.auth.naver.dto.NaverUserInfoResponse;
import com.pyxis.backend.user.UserRepository;
import com.pyxis.backend.user.entity.UserRole;
import com.pyxis.backend.user.entity.UserSocialType;
import com.pyxis.backend.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NaverLoginService {

    private final NaverApiClient naverApiClient;
    private final UserRepository userRepository;

    @Transactional
    public Users naverLogin(String code, String state) {

        // 1) Access Token 요청
        NaverTokenResponse token = naverApiClient.requestToken(code, state);

        // 2) 사용자 정보 요청
        NaverUserInfoResponse info = naverApiClient.requestUserInfo(token.getAccess_token());

        NaverUserInfoResponse.Response userInfo = info.getResponse();
        String naverId = userInfo.getId();

        // 3) 기존 사용자 있는지 확인
        return userRepository.findBySocialIdAndSocialType(naverId, UserSocialType.NAVER)
                .orElseGet(() -> createNewNaverUser(userInfo));
    }

    /**
     * 신규 네이버 회원 생성
     */
    private Users createNewNaverUser(NaverUserInfoResponse.Response userInfo) {

        String baseNickname = Optional.ofNullable(userInfo.getNickname())
                .orElse("NAVER_USER");

        String nickname = generateNickname(baseNickname);

        Users user = Users.builder()
                .loginId("naver_" + userInfo.getId())  // 로그인 식별자
                .socialId(userInfo.getId())
                .nickname(nickname)
                .role(UserRole.USER)
                .socialType(UserSocialType.NAVER)
                .build();

        return userRepository.save(user);
    }

    /**
     * 닉네임 중복 방지
     */
    private String generateNickname(String baseNickname) {

        if (baseNickname == null || baseNickname.isBlank()) {
            return "NAVER_" + System.currentTimeMillis();
        }

        String nickname = baseNickname;
        int count = 1;

        while (userRepository.existsByNickname(nickname)) {
            nickname = baseNickname + "_" + count;
            count++;
        }

        return nickname;
    }

    public String generateState() {
        return UUID.randomUUID().toString();
    }
}
