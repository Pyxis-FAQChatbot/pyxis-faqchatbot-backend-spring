package com.pyxis.backend.chat.botchat;

import com.pyxis.backend.chat.botchat.dto.BotchatListResponse;
import com.pyxis.backend.chat.botchat.entity.Botchat;
import com.pyxis.backend.user.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BotchatRepository extends JpaRepository<Botchat, Long> {

    @Query("SELECT new com.pyxis.backend.chat.botchat.dto.BotchatListResponse(" +
            "b.id, " +
            "b.title, " +
            "b.createdAt, " +
            "(SELECT MAX(m.createdAt) FROM BotMessage m WHERE m.botchat.id = b.id), " +
            "(SELECT m.userQuery FROM BotMessage m WHERE m.botchat.id = b.id ORDER BY m.createdAt DESC LIMIT 1)) " +
            "FROM Botchat b " +
            "WHERE b.user.id = :userId " +
            "ORDER BY COALESCE((SELECT MAX(m.createdAt) FROM BotMessage m WHERE m.botchat.id = b.id), b.createdAt) DESC")
    Page<BotchatListResponse> findByUserIdWithLastMessage(@Param("userId") Long userId, Pageable pageable);

    Optional<Botchat> findByIdAndUser(Long chatbotId, Users user);
}
