package com.gym.crm.app.security;

import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.AuthentificationErrorException;
import com.gym.crm.app.mapper.UserMapper;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.repository.UserRepository;
import com.gym.crm.app.rest.LoginRequest;
import com.gym.crm.app.security.model.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticatedUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public AuthenticatedUser getAuthenticatedUser(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new AuthentificationErrorException("Invalid credentials"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthentificationErrorException("Invalid password");
        }

        UserRole role = defineUserRole(user);

        return userMapper.toAuthenticatedUser(user)
                .toBuilder()
                .role(role)
                .build();
    }

    public UserRole defineUserRole(User user) {
        if (trainerRepository.findByUserUsername(user.getUsername()).isPresent()) {
            return UserRole.TRAINER;
        } else if (traineeRepository.findByUserUsername(user.getUsername()).isPresent()) {
            return UserRole.TRAINEE;
        } else {
            throw new AuthentificationErrorException("User does not belong to trainer or trainee roles");
        }
    }
}
