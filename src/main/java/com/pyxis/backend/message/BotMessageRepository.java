package com.pyxis.backend.message;

import com.pyxis.backend.message.entity.BotMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotMessageRepository extends JpaRepository<BotMessage, Long> {
}
