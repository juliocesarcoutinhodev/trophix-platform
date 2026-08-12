package com.trophix.api.users.application.ports.out;

import com.trophix.api.users.model.AccountLinkTicket;

import java.util.Optional;

public interface AccountLinkRepositoryPort {

    /**
     * Persists a ticket, replacing any previous pending ticket for the same psnId.
     */
    void save(AccountLinkTicket ticket);

    Optional<AccountLinkTicket> findByPsnId(String psnId);

    /**
     * Removes the ticket after a successful validation (single-use token).
     */
    void deleteByPsnId(String psnId);
}