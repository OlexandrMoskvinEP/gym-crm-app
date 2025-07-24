package com.gym.crm.app.security;

import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.AuthentificationErrorException;
import com.gym.crm.app.exception.AuthorizationErrorException;
import com.gym.crm.app.exception.UnacceptableOperationException;
import com.gym.crm.app.mapper.UserMapper;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.repository.UserRepository;
import com.gym.crm.app.rest.LoginRequest;
import com.gym.crm.app.security.model.AuthenticatedUser;
import com.gym.crm.app.security.model.UserCredentialsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

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
                .orElseThrow(() -> new AuthentificationErrorException("Invalid credentials"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthentificationErrorException("Invalid password");
        }

        UserRole role = defineUserRole(user);
        AuthenticatedUser authenticatedUser = userMapper.toAuthenticatedUser(user)
                .toBuilder()
                .role(role)
                .build();

        currentUserHolder.set(authenticatedUser);
    }

    public void checkUserAuthorisation(UserCredentialsDto credentials, UserRole... allowedRoles) {
        AuthenticatedUser currentUser = currentUserHolder.get();

        if(currentUser == null){
            throw new UnacceptableOperationException("User is not logged in");
        }

        if (!currentUser.getUsername().equals(credentials.getUsername())) {
            throw new UnacceptableOperationException("User cannot perform this operation on behalf of another user");
        }

        String username = credentials.getUsername();
        String rawPassword = credentials.getPassword();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthorizationErrorException(("User with such username does not exist")));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new AuthorizationErrorException("User cannot be authenticated - invalid password");
        }

        UserRole actualRole = defineUserRole(user);
        boolean isRoleAllowed = Arrays.asList(allowedRoles).contains(actualRole);

        if (!isRoleAllowed) {
            throw new AuthorizationErrorException("User role [" + actualRole + "] is not allowed for this operation");
        }

        log.info("User [{}] successfully authenticated and authorized as [{}]", username, actualRole);
    }

    private UserRole defineUserRole(User user) {
        if (trainerRepository.findByUsername(user.getUsername()).isPresent()) {
            return UserRole.TRAINER;
        } else if (traineeRepository.findByUsername(user.getUsername()).isPresent()) {
            return UserRole.TRAINEE;
        } else {
            throw new AuthentificationErrorException("User does not belong to trainer or trainee roles");
        }
    }
}
