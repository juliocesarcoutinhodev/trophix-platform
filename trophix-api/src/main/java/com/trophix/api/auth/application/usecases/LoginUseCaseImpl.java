package com.trophix.api.auth.application.usecases;

import com.trophix.api.auth.application.ports.in.LoginUseCase;
import com.trophix.api.auth.application.ports.out.PasswordEncoderPort;
import com.trophix.api.auth.application.ports.out.TokenGeneratorPort;
import com.trophix.api.shared.exception.BusinessException;
import com.trophix.api.users.application.ports.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginUseCaseImpl implements LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;

    @Override
    public String login(LoginCommand command) {
        // 1. Busca usuário por e-mail
        var user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas."));

        // 2. Verifica a senha
        if (user.password() == null || !passwordEncoder.matches(command.plainPassword(), user.password())) {
            throw new BusinessException("Credenciais inválidas.");
        }

        // 3. Monta lista de roles
        List<String> roles = user.roles().stream()
                .map(role -> role.name())
                .toList();

        // 4. Gera o JWT com o userId como subject (identificador estável)
        String token = tokenGenerator.generate(user.id().toString(), roles);
        log.info("Login realizado para email={}", command.email());

        return token;
    }
}
