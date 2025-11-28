package com.pyxis.backend.auth.kakao;

import com.pyxis.backend.auth.kakao.config.KakaoApiClient;
import com.pyxis.backend.auth.kakao.dto.KakaoTokenResponse;
import com.pyxis.backend.auth.kakao.dto.KakaoUserInfo;
import com.pyxis.backend.user.UserRepository;
import com.pyxis.backend.user.entity.UserRole;
import com.pyxis.backend.user.entity.UserSocialType;
import com.pyxis.backend.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    private final KakaoApiClient kakaoApiClient;
    private final UserRepository userRepository;

    @Transactional
    public Users kakaoLogin(String code) {

        KakaoTokenResponse token = kakaoApiClient.requestToken(code);
        KakaoUserInfo info = kakaoApiClient.requestUserInfo(token.getAccess_token());

        String kakaoId = info.getId().toString();

        // 기존 회원이면 그대로 반환
        return userRepository.findBySocialIdAndSocialType(kakaoId, UserSocialType.KAKAO)
                .orElseGet(() -> createNewKakaoUser(info));
    }


    private Users createNewKakaoUser(KakaoUserInfo info) {

        String baseNickname = Optional.ofNullable(info.getKakao_account())
                .map(KakaoUserInfo.KakaoAccount::getProfile)
                .map(KakaoUserInfo.KakaoAccount.Profile::getNickname)
                .orElse(null);

        String nickname = generateNickname(baseNickname);

        Users user = Users.builder()
                .loginId("kakao_"+info.getId())
                .socialId(info.getId().toString())
                .nickname(nickname)
                .role(UserRole.USER)
                .socialType(UserSocialType.KAKAO)
                .build();

        return userRepository.save(user);
    }


    private String generateNickname(String baseNickname) {

        // 닉네임이 없는 경우 → 가장 안전한 fallback
        if (baseNickname == null || baseNickname.isBlank()) {
            return "KAKAO_" + System.currentTimeMillis();
        }

        String nickname = baseNickname;
        int count = 1;

        while (userRepository.existsByNickname(nickname)) {
            nickname = baseNickname + "_" + count;
            count++;
        }

        return nickname;
    }
}

