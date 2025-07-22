package com.gym.crm.app.security;

import com.gym.crm.app.security.model.UserCredentialsDto;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.AuthentificationException;
import com.gym.crm.app.mapper.UserMapper;
import com.gym.crm.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserHolder currentUserHolder;
    private final UserMapper userMapper;

    public void authenticate(UserCredentialsDto credentials) {
        String username = credentials.getUsername();
        String rawPassword = credentials.getPassword();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthentificationException("User with such username does not exist"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new AuthentificationException("User cannot be authenticated - invalid credentials");
        }

        log.info("User [{}] authenticated successfully", username);

        currentUserHolder.set(userMapper.toAuthenticatedUser(user));
    }
}
