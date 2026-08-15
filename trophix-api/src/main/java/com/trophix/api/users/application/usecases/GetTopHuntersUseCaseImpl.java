package com.trophix.api.users.application.usecases;

import com.trophix.api.users.application.ports.in.GetTopHuntersUseCase;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetTopHuntersUseCaseImpl implements GetTopHuntersUseCase {

    private static final int MAX_LIMIT = 100;

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> getTopHunters(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, MAX_LIMIT));
        return userRepository.findTopHunters(PageRequest.of(0, safeLimit));
    }
}
