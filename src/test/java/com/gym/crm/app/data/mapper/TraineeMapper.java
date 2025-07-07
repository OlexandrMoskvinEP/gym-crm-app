package com.gym.crm.app.data.mapper;

import com.gym.crm.app.domain.model.Trainee;

import java.time.LocalDate;

import static com.gym.crm.app.data.mapper.UserMapper.constructUser;

public class TraineeMapper {
    public static Trainee constructTrainee() {
        return Trainee.builder()
                .address("Fake test address")
                .dateOfBirth(LocalDate.now())
                .build();
    }

    public Trainee constructTraineeWithUser() {
        return constructTrainee()
                .toBuilder()
                .user(constructUser())
                .build();
    }
}
