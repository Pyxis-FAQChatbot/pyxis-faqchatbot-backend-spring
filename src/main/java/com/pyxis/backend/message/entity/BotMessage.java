package com.pyxis.backend.message.entity;

import com.pyxis.backend.chat.botchat.entity.Botchat;
import com.pyxis.backend.common.converter.SourceDataListConverter;
import com.pyxis.backend.message.dto.SourceData;
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
public class BotMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "botchat_id", nullable = false)
    private Botchat botchat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String userQuery;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String botResponse;

    @Convert(converter = SourceDataListConverter.class)
    @Column(name = "source_data", columnDefinition = "JSON")
    @Builder.Default
    private List<SourceData> sourceData = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}