package com.gym.crm.app.service.common;

import com.gym.crm.app.domain.dto.user.ChangeActivationStatusDto;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.AuthentificationErrorException;
import com.gym.crm.app.exception.CoreServiceException;
import com.gym.crm.app.exception.DataBaseErrorException;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
@Validated
public class UserProfileService {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

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

    @Transactional
    public void changePassword(String username, String oldPassword, @NotNull String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new DataBaseErrorException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AuthentificationErrorException("Invalid old password");
        }

        User updatedUser = user.toBuilder()
                .password(passwordEncoder.encode(newPassword))
                .build();

        userRepository.save(updatedUser);
    }

    @Transactional
    public void switchActivationStatus(@Valid ChangeActivationStatusDto dto) {
        String username = dto.getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new DataBaseErrorException(
                        String.format("User with username %s not found", username)));

        if (Objects.equals(user.getIsActive(), dto.getIsActive())) {
            String status = dto.getIsActive() ? "activate" : "deactivate";
            throw new CoreServiceException(format(
                    "Could not %s user %s: user is already %sed", status, username, status));
        }

        User updatedUser = user.toBuilder().isActive(dto.getIsActive()).build();
        userRepository.save(updatedUser);
    }

    public boolean isUsernameAlreadyExists(String username) {
        return retrieveAllUsernames().stream().anyMatch(existingUsername -> existingUsername.equals(username));
    }

    private List<String> retrieveAllUsernames() {
        Stream<String> traineeUsernames = traineeRepository.findAll().stream()
                .map(trainee -> trainee.getUser().getUsername());
        Stream<String> trainerUsernames = trainerRepository.findAll().stream()
                .map(trainee -> trainee.getUser().getUsername());

        return Stream.concat(traineeUsernames, trainerUsernames).toList();
    }
}
