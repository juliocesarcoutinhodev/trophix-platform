package com.trophix.api.users.application.usecases;

import com.trophix.api.shared.exception.BusinessException;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import com.trophix.api.users.application.ports.in.ValidateAccountLinkUseCase;
import com.trophix.api.users.application.ports.out.AccountLinkRepositoryPort;
import com.trophix.api.users.application.ports.out.PsnProfileFetcherPort;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.AccountLinkValidation;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class ValidateAccountLinkUseCaseImpl implements ValidateAccountLinkUseCase {

    private final AccountLinkRepositoryPort accountLinkRepository;
    private final PsnProfileFetcherPort psnProfileFetcher;
    private final UserRepository userRepository;

    @Override
    public AccountLinkValidation validateLink(String psnId) {
        // 1. Busca ticket pendente
        var ticket = accountLinkRepository.findByPsnId(psnId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Solicitacao de vinculacao nao encontrada. Solicite um novo token."));

        // 2. Verifica expiração
        if (ticket.expiresAt().isBefore(Instant.now())) {
            throw new BusinessException("Token de verificacao expirado. Solicite um novo token.");
        }

        // 3. Busca perfil real na PSN via sidecar
        var profile = psnProfileFetcher.fetchByPsnId(psnId);
        String aboutMe = profile.aboutMe() == null ? "" : profile.aboutMe();

        // 4. Verifica se o token está no "About Me"
        if (!aboutMe.contains(ticket.verificationToken())) {
            throw new BusinessException(
                    "Token de verificacao nao encontrado no perfil publico da PSN.");
        }

        // 5. Token válido — invalida o ticket (uso único)
        accountLinkRepository.deleteByPsnId(psnId);

        // 6. Cria ou recupera o usuário vinculado
        User user = userRepository.findByUsername(psnId)
                .orElseGet(() -> {
                    User newUser = User.createFromPsn(psnId, profile.avatarUrl());
                    log.info("Novo usuario criado via PSN: psnId={} userId={}", psnId, newUser.id());
                    return userRepository.save(newUser);
                });

        log.info("Conta PSN vinculada com sucesso: psnId={} userId={}", psnId, user.id());
        return new AccountLinkValidation(user.id(), psnId, "Conta vinculada com sucesso!");
    }
}