package com.pyxis.backend.message;

import com.pyxis.backend.chat.botchat.entity.Botchat;
import com.pyxis.backend.message.entity.BotMessage;
import com.pyxis.backend.user.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BotMessageRepository extends JpaRepository<BotMessage, Long> {

    @Query("SELECT m FROM BotMessage m " +
            "WHERE m.botchat.id = :botchatId " +
            "AND m.botchat.user.id = :userId " +
            "ORDER BY m.createdAt DESC")
    Page<BotMessage> findByBotchatIdAndUserId(
            @Param("botchatId") Long botchatId,
            @Param("userId") Long userId,
            Pageable pageable
    );

}
