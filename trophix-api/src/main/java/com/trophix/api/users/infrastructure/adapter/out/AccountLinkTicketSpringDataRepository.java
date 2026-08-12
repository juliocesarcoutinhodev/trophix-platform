package com.trophix.api.users.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountLinkTicketSpringDataRepository
        extends JpaRepository<AccountLinkTicketJpaEntity, UUID> {

    Optional<AccountLinkTicketJpaEntity> findByPsnId(String psnId);

    void deleteByPsnId(String psnId);
}