package com.gym.crm.app.domain.dto;

import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.TrainingType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TrainingDto {
    private int trainerId;
    private int traineeId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private int trainingDuration;

    public Trainer getTrainer() {
        return new Trainer();
    }

    public Trainee getTrainee() {
        return new Trainee();
    }
}
