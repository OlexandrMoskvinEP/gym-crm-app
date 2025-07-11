package com.gym.crm.app.service.common;

import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class UserProfileService {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    private final PasswordService passwordService;

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

    public boolean changePassword(String username, String password) {
        String encodedPassword = passwordService.encodePassword(password);

        try {
            userRepository.updatePassword(username, encodedPassword);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private List<String> retrieveAllUsernames() {
        Stream<String> traineeUsernames = traineeRepository.findAll().stream()
                .map(trainee -> trainee.getUser().getUsername());
        Stream<String> trainerUsernames = trainerRepository.findAll().stream()
                .map(trainee -> trainee.getUser().getUsername());

        return Stream.concat(traineeUsernames, trainerUsernames).toList();
    }
}
