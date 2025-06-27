package com.gym.crm.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PasswordGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
    private static final int PASSWORD_LENGTH = 10;

    private SecureRandom random;

    @Autowired
    public void setRandom(SecureRandom random) {
        this.random = random;
    }

    private final AtomicInteger traineeCounter = new AtomicInteger(1);
    private final AtomicInteger trainerCounter = new AtomicInteger(1);
    private final AtomicInteger trainingCounter = new AtomicInteger(1);


    public String generateTraineeId() {
        return "Trainee-" + traineeCounter.getAndIncrement();
    }

    public int generateTrainerId() {
        return trainerCounter.getAndIncrement();
    }

    public String generateTrainingId() {
        return "Training-" + trainingCounter.getAndIncrement();
    }

    public String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }

    public String generateUsername(String firstName, String lastName) {
        return firstName + "." + lastName;
    }

    public String addIndexWhenDuplicate(String userName, int index) {
        return userName + index;
    }
}
