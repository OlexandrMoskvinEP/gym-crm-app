package com.gym.crm.app.service.utils;

import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class UserProfileService {
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public String createUsername(String firstName, String lastName) {
        String rawUsername = firstName + "." + lastName;
        String username = rawUsername;
        int suffix = 1;

        List<String> allExistUsernames = retrieveAllUsernames();

        while (allExistUsernames.contains(username)) {
            username = rawUsername + suffix++;
        }
        return username;
    }

    private List<String> retrieveAllUsernames() {
        Stream<String> traineeUsernames = traineeRepository.findAll().stream()
                .map(User::getUsername);
        Stream<String> trainerUsernames = trainerRepository.findAll().stream()
                .map(User::getUsername);

        return Stream.concat(traineeUsernames, trainerUsernames).toList();
    }
}
