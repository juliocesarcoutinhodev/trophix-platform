package com.trophix.api.users.application.usecases;

import com.trophix.api.shared.domain.UuidV7;
import com.trophix.api.users.application.ports.in.RequestAccountLinkUseCase;
import com.trophix.api.users.application.ports.out.AccountLinkRepositoryPort;
import com.trophix.api.users.model.AccountLinkTicket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class RequestAccountLinkUseCaseImpl implements RequestAccountLinkUseCase {

    private static final String TOKEN_PREFIX = "TRFX-";
    private static final Duration TICKET_TTL = Duration.ofMinutes(15);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AccountLinkRepositoryPort accountLinkRepository;

    @Override
    public String requestLink(String psnId) {
        String verificationToken = generateVerificationToken();
        Instant expiresAt = Instant.now().plus(TICKET_TTL);

        AccountLinkTicket ticket = new AccountLinkTicket(
                UuidV7.generate(), psnId, verificationToken, expiresAt);

        accountLinkRepository.save(ticket);
        log.info("Ticket de vinculacao gerado para psnId={} expiraEm={}", psnId, expiresAt);

        return verificationToken;
    }

    private static String generateVerificationToken() {
        return TOKEN_PREFIX + String.format("%04d", SECURE_RANDOM.nextInt(10_000));
    }
}