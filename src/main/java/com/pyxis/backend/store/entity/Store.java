package com.pyxis.backend.store.entity;

import com.pyxis.backend.store.dto.StoreUpdateRequest;
import com.pyxis.backend.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String industryCode;

    @Column(nullable = false)
    private String address;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void update(StoreUpdateRequest request) {
        if(request.getStoreName() != null &&  !request.getStoreName().isEmpty())
            this.name = request.getStoreName();
        if(request.getAddress() != null &&  !request.getAddress().isEmpty())
            this.address = request.getAddress();
        if(request.getIndustryCode() != null && !request.getIndustryCode().isEmpty()){
            this.industryCode = request.getIndustryCode();
        }
    }

}
