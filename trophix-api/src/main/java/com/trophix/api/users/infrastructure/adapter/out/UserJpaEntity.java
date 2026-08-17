package com.trophix.api.users.infrastructure.adapter.out;

import com.trophix.api.shared.infrastructure.adapter.out.RoleJpaEntity;
import com.trophix.api.shared.infrastructure.persistence.UuidV7Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserJpaEntity {

    @Id
    @UuidV7Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String username;

    @Column(nullable = true, length = 320)
    private String email;

    @Column(nullable = true, length = 255)
    private String password;

    @Column(length = 500)
    private String avatarUrl;

    @Column(length = 64)
    private String accountId;

    private Integer psnLevel;

    private Integer levelProgress;

    private Integer totalPlatinum;

    private Integer totalGold;

    private Integer totalSilver;

    private Integer totalBronze;

    private Instant lastSyncedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleJpaEntity> roles = new HashSet<>();
}