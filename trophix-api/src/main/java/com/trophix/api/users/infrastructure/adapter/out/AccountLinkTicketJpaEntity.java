package com.trophix.api.users.infrastructure.adapter.out;

import com.trophix.api.shared.infrastructure.persistence.UuidV7Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "account_link_tickets")
@Getter
@Setter
public class AccountLinkTicketJpaEntity implements Persistable<UUID> {

    @Id
    @UuidV7Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String psnId;

    @Column(nullable = false, length = 9)
    private String verificationToken;

    @Column(nullable = false)
    private Instant expiresAt;

    @Transient
    private boolean isNew = true;

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}