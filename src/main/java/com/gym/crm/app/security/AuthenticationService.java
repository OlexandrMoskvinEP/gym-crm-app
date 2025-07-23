package com.gym.crm.app.security;

import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.rest.LoginRequest;
import com.gym.crm.app.security.model.AuthenticatedUser;
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
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public void login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new AuthentificationException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthentificationException("Invalid username or password");
        }

        UserRole role = defineUserRole(user);
        AuthenticatedUser authenticatedUser = userMapper.toAuthenticatedUser(user)
                .toBuilder()
                .role(role)
                .build();

        currentUserHolder.set(authenticatedUser);
    }

    public boolean authorisationFilter(UserCredentialsDto credentials) {
        String username = credentials.getUsername();
        String rawPassword = credentials.getPassword();
        String role = credentials.getRole();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthentificationException("User with such username does not exist"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new AuthentificationException("User cannot be authenticated - invalid credentials");
        }

        log.info("User [{}] authorise successfully", username);

        //currentUserHolder.set(userMapper.toAuthenticatedUser(user));
        //todo добавить проверку роли и в параметр добавить роль
        return currentUserHolder.get().equals(userMapper.toAuthenticatedUser(user));
    }

    private UserRole defineUserRole(User user) {
        if (trainerRepository.findByUsername(user.getUsername()).isPresent()) {
            return UserRole.TRAINER;
        } else if (traineeRepository.findByUsername(user.getUsername()).isPresent()) {
            return UserRole.TRAINEE;
        } else {
            throw new AuthentificationException("User does not belong to trainer or trainee roles");
        }
    }
}
