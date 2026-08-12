package com.trophix.api.shared.infrastructure.security;

import com.trophix.api.users.application.ports.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Loads user details by email for Spring Security's authentication machinery.
 * Used as a fallback / for future form-login or basic auth scenarios.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(user -> User.builder()
                        .username(user.email())
                        .password(user.password() != null ? user.password() : "")
                        .authorities(user.roles().stream()
                                .map(role -> new SimpleGrantedAuthority(role.name()))
                                .toList())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + email));
    }
}
