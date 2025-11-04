package com.pyxis.backend.chat.botchat;

import com.pyxis.backend.chat.botchat.entity.Botchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotchatRepository extends JpaRepository<Botchat, Long> {
}
