package com.trophix.api.auth.application.usecases;

import com.trophix.api.auth.application.ports.in.CompleteRegistrationUseCase;
import com.trophix.api.auth.application.ports.out.PasswordEncoderPort;
import com.trophix.api.shared.application.ports.out.RoleRepositoryPort;
import com.trophix.api.shared.model.Role;
import com.trophix.api.shared.domain.UuidV7;
import com.trophix.api.shared.exception.BusinessException;
import com.trophix.api.shared.exception.DataIntegrityException;
import com.trophix.api.users.application.ports.out.PsnProfileFetcherPort;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class CompleteRegistrationUseCaseImpl implements CompleteRegistrationUseCase {

    private static final String ROLE_USER = "ROLE_USER";

    private final UserRepository userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final PsnProfileFetcherPort psnProfileFetcher;

    @Override
    public User completeRegistration(RegistrationCommand command) {
        User existing = userRepository.findByUsername(command.psnId()).orElse(null);

        if (existing != null) {
            userRepository.findByEmail(command.email())
                    .filter(other -> !other.id().equals(existing.id()))
                    .ifPresent(other -> {
                        throw new BusinessException("Este e-mail já está em uso.");
                    });
        }

        Role userRole = roleRepository.findByName(ROLE_USER)
                .orElseThrow(() -> new DataIntegrityException(
                        "Role ROLE_USER não encontrada no banco. Contate o administrador."));

        String hashedPassword = passwordEncoder.encode(command.plainPassword());

        String avatarUrl = psnProfileFetcher.fetchByPsnId(command.psnId()).avatarUrl();

        User userToSave = existing != null
                ? new User(existing.id(), existing.username(), command.email(),
                        hashedPassword,
                        avatarUrl != null ? avatarUrl : existing.avatarUrl(),
                        Set.of(userRole),
                        existing.accountId(), existing.psnLevel(), existing.levelProgress(),
                        existing.totalPlatinum(), existing.totalGold(),
                        existing.totalSilver(), existing.totalBronze(),
                        existing.lastSyncedAt())
                : new User(UuidV7.generate(), command.psnId(), command.email(),
                        hashedPassword, avatarUrl, Set.of(userRole),
                        null, null, null, null, null, null, null, null);

        User savedUser = userRepository.save(userToSave);
        log.info("Cadastro finalizado para psnId={} email={}", command.psnId(), command.email());

        return savedUser;
    }
}
