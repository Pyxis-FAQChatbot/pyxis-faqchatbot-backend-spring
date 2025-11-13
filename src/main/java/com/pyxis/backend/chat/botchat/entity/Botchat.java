package com.pyxis.backend.chat.botchat.entity;

import com.pyxis.backend.message.entity.BotMessage;
import com.pyxis.backend.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Botchat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // oracle 나중에 시퀀스로 변경예정
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    private String title;

    @OneToMany(mappedBy = "botchat", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BotMessage> messages = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void updateTitle(String title) {
        this.title = title;
    }
}
