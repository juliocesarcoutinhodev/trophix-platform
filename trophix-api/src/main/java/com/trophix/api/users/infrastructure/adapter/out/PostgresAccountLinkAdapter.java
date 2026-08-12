package com.trophix.api.users.infrastructure.adapter.out;

import com.trophix.api.users.application.ports.out.AccountLinkRepositoryPort;
import com.trophix.api.users.model.AccountLinkTicket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostgresAccountLinkAdapter implements AccountLinkRepositoryPort {

    private final AccountLinkTicketSpringDataRepository springDataRepository;

    @Override
    public void save(AccountLinkTicket ticket) {
        AccountLinkTicketJpaEntity entity = springDataRepository.findByPsnId(ticket.psnId())
                .map(existing -> {
                    existing.setVerificationToken(ticket.verificationToken());
                    existing.setExpiresAt(ticket.expiresAt());
                    return existing;
                })
                .orElseGet(() -> {
                    AccountLinkTicketJpaEntity created = new AccountLinkTicketJpaEntity();
                    created.setId(ticket.id());
                    created.setPsnId(ticket.psnId());
                    created.setVerificationToken(ticket.verificationToken());
                    created.setExpiresAt(ticket.expiresAt());
                    return created;
                });

        springDataRepository.save(entity);
    }

    @Override
    public Optional<AccountLinkTicket> findByPsnId(String psnId) {
        return springDataRepository.findByPsnId(psnId).map(this::toDomain);
    }

    @Override
    @Transactional
    public void deleteByPsnId(String psnId) {
        springDataRepository.deleteByPsnId(psnId);
    }

    private AccountLinkTicket toDomain(AccountLinkTicketJpaEntity entity) {
        return new AccountLinkTicket(
                entity.getId(),
                entity.getPsnId(),
                entity.getVerificationToken(),
                entity.getExpiresAt());
    }
}