package com.pyxis.backend.user;

import com.pyxis.backend.user.entity.UserSocialType;
import com.pyxis.backend.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    boolean existsByNickname(String nickname);

    boolean existsByLoginId(String loginId);

    Optional<Users> findByLoginId(String loginId);

    Optional<Users> findBySocialIdAndSocialType(String socialId, UserSocialType type);

}
