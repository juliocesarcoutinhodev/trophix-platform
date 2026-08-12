package com.trophix.api.users.application.usecases;

import com.trophix.api.shared.exception.ResourceNotFoundException;
import com.trophix.api.users.application.ports.in.GetUserProfileUseCase;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetUserProfileUseCaseImpl implements GetUserProfileUseCase {

    private final UserRepository userRepository;

    @Override
    public User getProfile(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    @Override
    public User getProfileByUserId(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }
}