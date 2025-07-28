package com.gym.crm.app.service.common;

import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.AuthentificationErrorException;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class UserProfileService {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    private final PasswordService passwordService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public String createUsername(String firstName, String lastName) {
        String rawUsername = firstName + "." + lastName;
        String username = rawUsername.toLowerCase();
        int suffix = 1;

        List<String> allExistUsernames = retrieveAllUsernames().stream().map(String::toLowerCase).toList();

        while (allExistUsernames.contains(username)) {
            username = rawUsername + suffix++;
        }

        if (!rawUsername.equalsIgnoreCase(username)) {
            logger.warn("User with username {} already exist, applying index", rawUsername);
        }

        return username;
    }

    public void changePassword(String username, String oldPassword, @NotNull String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String existingEncodedPassword = user.getPassword();

        if (!passwordEncoder.matches(oldPassword, existingEncodedPassword)) {
            throw new AuthentificationErrorException("Invalid old password");
        }

        String newEncodedPassword = passwordEncoder.encode(newPassword);
        userRepository.updatePassword(username, newEncodedPassword);
    }

    public void switchActivationStatus(String username) {
        userRepository.changeStatus(username);
    }

    private List<String> retrieveAllUsernames() {
        Stream<String> traineeUsernames = traineeRepository.findAll().stream()
                .map(trainee -> trainee.getUser().getUsername());
        Stream<String> trainerUsernames = trainerRepository.findAll().stream()
                .map(trainee -> trainee.getUser().getUsername());

        return Stream.concat(traineeUsernames, trainerUsernames).toList();
    }
}
