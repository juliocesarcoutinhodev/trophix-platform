package com.trophix.api.users.application.usecases;

import com.trophix.api.shared.exception.ResourceNotFoundException;
import com.trophix.api.shared.exception.SyncCooldownException;
import com.trophix.api.users.application.async.UserProfileSyncExecutor;
import com.trophix.api.users.application.ports.in.SyncUserProfileUseCase;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class SyncUserProfileUseCaseImpl implements SyncUserProfileUseCase {

    private static final Duration SYNC_COOLDOWN = Duration.ofMinutes(15);

    private final UserRepository userRepository;
    private final UserProfileSyncExecutor syncExecutor;

    @Override
    public void requestSync(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        assertNotInCooldown(user);

        userRepository.updateLastSyncedAt(userId, Instant.now());
        syncExecutor.syncProfileAsync(userId);

        log.info("Sincronização agendada para userId={}", userId);
    }

    private void assertNotInCooldown(User user) {
        Instant lastSyncedAt = user.lastSyncedAt();
        if (lastSyncedAt == null) {
            return;
        }

        Duration elapsed = Duration.between(lastSyncedAt, Instant.now());
        if (elapsed.compareTo(SYNC_COOLDOWN) >= 0) {
            return;
        }

        long secondsLeft = SYNC_COOLDOWN.minus(elapsed).getSeconds();
        long minutesRemaining = Math.max(1, (secondsLeft + 59) / 60);
        String minutesLabel = minutesRemaining > 1 ? "minutos." : "minuto.";

        throw new SyncCooldownException(
                "Você sincronizou seus dados recentemente. Tente novamente em "
                        + minutesRemaining + " " + minutesLabel,
                minutesRemaining);
    }
}