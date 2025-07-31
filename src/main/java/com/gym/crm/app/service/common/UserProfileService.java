package com.gym.crm.app.service.common;

import com.gym.crm.app.domain.dto.user.ChangeActivationStatusDto;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.AuthentificationErrorException;
import com.gym.crm.app.exception.CoreServiceException;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
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

    public void switchActivationStatus(@Valid ChangeActivationStatusDto changeActivationStatusDto) {
        String username = changeActivationStatusDto.getUsername();

        boolean isCurrentlyActivated = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(format("User %s not found", username)))
                .getIsActive();

        if (isCurrentlyActivated == changeActivationStatusDto.getIsActive()) {
            String status = isCurrentlyActivated ? "activate" : "deactivate";
            throw new CoreServiceException(format("Could not %s user %s: user is already %sed", status, username, status));
        }

        userRepository.changeStatus(changeActivationStatusDto.getUsername());
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
