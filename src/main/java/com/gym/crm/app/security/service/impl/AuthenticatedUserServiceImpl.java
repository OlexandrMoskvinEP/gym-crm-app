package com.gym.crm.app.security.service.impl;

import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.AuthentificationErrorException;
import com.gym.crm.app.exception.TooManyRequestsAuthExceptions;
import com.gym.crm.app.mapper.UserMapper;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.repository.UserRepository;
import com.gym.crm.app.rest.LoginRequest;
import com.gym.crm.app.security.model.AuthenticatedUser;
import com.gym.crm.app.security.model.UserRole;
import com.gym.crm.app.security.service.AuthenticatedUserService;
import com.gym.crm.app.security.service.LoginAttemptsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticatedUserServiceImpl implements AuthenticatedUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final LoginAttemptsService loginAttemptsService;

    @Override
    public AuthenticatedUser getAuthenticatedUser(LoginRequest loginRequest) {
        if (loginAttemptsService.isBlocked(loginRequest.getUsername())) {
            Long retryAfter = loginAttemptsService.getRetryAfterSeconds(loginRequest.getUsername());

            log.warn("Login blocked for user {}", loginRequest.getUsername());
            throw new TooManyRequestsAuthExceptions("Too many attempts. Try later.", retryAfter);
        }

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new AuthentificationErrorException("Invalid credentials"));

        checkCredentials(loginRequest, user);
        loginAttemptsService.loginSucceeded(loginRequest.getUsername());
        log.debug("User {} logged in successfully", loginRequest.getUsername());

        UserRole role = resolveUserRole(user.getUsername());

        return userMapper.toAuthenticatedUser(user)
                .toBuilder()
                .role(role)
                .build();
    }

    @Override
    public UserRole resolveUserRole(String username) {
        if (trainerRepository.findByUserUsername(username).isPresent()) {
            return UserRole.TRAINER;
        } else if (traineeRepository.findByUserUsername(username).isPresent()) {
            return UserRole.TRAINEE;
        } else {
            throw new AuthentificationErrorException("User does not belong to trainer or trainee roles");
        }
    }

    private void checkCredentials(LoginRequest loginRequest, User user) {
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            loginAttemptsService.loginFailed(loginRequest.getUsername());

            throw new AuthentificationErrorException("Invalid password");
        }
    }
}
